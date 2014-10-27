package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.*;

import com.sun.jndi.cosnaming.IiopUrl;
import uk.ac.dundee.computing.aec.instagrim.Constants;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Converters;
import uk.ac.dundee.computing.aec.instagrim.models.AddressModel;
import uk.ac.dundee.computing.aec.instagrim.models.CommentModel;
import uk.ac.dundee.computing.aec.instagrim.stores.AddressBean;
import uk.ac.dundee.computing.aec.instagrim.stores.CommentBean;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Profile;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@WebServlet(name = "Profile", urlPatterns = {"/profile/*", "/profile"})
public class ServletProfile extends HttpServlet {

    // This is the Cassandra cluster where this instance is going to get data from.
    private Cluster cluster = null;

    /**
     *
     * @param config
     */
    public void init(ServletConfig config) {
        try {
            this.cluster = CassandraHosts.getCluster();
        } catch(Exception e) {
            if(Constants.DEBUG) {
                System.out.println("---- Error at Login Servlet init ----\n\n");
                e.printStackTrace();
            }
        }
    }

    /**
     * This Servlet is going to receive a post request when a user modifies their profile with a form.
     * Then we will get the content of the new profile, which we will update in this user's profile row in the database.
     *
     * This is stored in the table `instagrim.userprofiles`#`profile_content`.
     *
     * @param request the request that was made as POST
     * @param response our response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String content = request.getParameter("content");
        String city = request.getParameter("city");
        String street = request.getParameter("street");
        String name = request.getParameter("name");
        String zipString = request.getParameter("zip");
        int zip = 0;
        if(!zipString.equals("")) {
            zip = Integer.parseInt(zipString);
        }
        if(Constants.VERBOSE) {
            System.out.println("New profile content: " + content);
            String[] contentArray = content.split("\r\n");
            for(String line : contentArray) {
                System.out.println(line);
            }
        }

        LoggedIn lg = (LoggedIn)request.getSession().getAttribute("LoggedIn");
        String username = lg.getUsername();
        AddressBean address = new AddressBean();
        address.setCity(city);
        address.setName(name);
        address.setStreet(street);
        address.setZip(zip);

        UserType addressUDT = cluster.getMetadata().getKeyspace("instagrim_js").getUserType("address");

        UDTValue addressObject = addressUDT.newValue()
                .setString("street", address.getStreet())
                .setString("city", address.getCity())
                .setInt("zip", address.getZip());

        String updateQuery = "";

        if(!city.equals("") && !name.equals("") && !street.equals("") && zip != 0) {
            updateQuery = "UPDATE userprofiles SET profile_content = ?, addresses = addresses + ? WHERE login = ?";
        } else {
            updateQuery = "UPDATE userprofiles SET profile_content = ? WHERE login = ?";
        }

        Session session = cluster.connect("instagrim_js");
        PreparedStatement ps = session.prepare(updateQuery);
        BoundStatement boundStatement = new BoundStatement(ps);

        Map<String, UDTValue> addresses = new HashMap<String, UDTValue>();
        addresses.put(address.getName(), addressObject);

        if(!city.equals("") && !name.equals("") && !street.equals("") && zip != 0) {
            session.execute(boundStatement.bind(content, addresses, username));
        } else {
            session.execute(boundStatement.bind(content, username));
        }

        session.close();

        try {
            response.sendRedirect("/instagrim-js/profile/" + username);
        } catch(IOException ioe) {
            if(Constants.VERBOSE) {
                System.out.println("Error at redirect after profile post.");
            }
            if(Constants.DEBUG) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        LoggedIn lg = (LoggedIn)request.getSession().getAttribute("LoggedIn");
        String username = "";
        if(lg != null) {
            username = lg.getUsername();
        }

        String args[] = Converters.SplitRequestPath(request);

        try {
            username = args[2];
        } catch(NullPointerException|ArrayIndexOutOfBoundsException e) {
            if(Constants.VERBOSE) {
                System.out.println("Error getting user profile for profile display.");
                System.out.println("Maybe the URL was manually modified?");
            }
            if(Constants.DEBUG) {
                e.printStackTrace();
            }
        }

        try {
            Session session = cluster.connect("instagrim_js");
            PreparedStatement ps = session.prepare("SELECT profile_content, userid, addresses FROM userprofiles WHERE login = ? LIMIT 1");
            ResultSet rs;
            BoundStatement boundStatement = new BoundStatement(ps);

            rs = session.execute(boundStatement.bind(username));

            if(rs.isExhausted()) {
                System.out.println("Couldn't find a profile for that user");
            } else {
                Profile profile = new Profile();
                Row r = rs.one();
                profile.setContent(r.getString("profile_content"));
                profile.setUserID(r.getUUID("userid"));
                profile.setUsername(username);

                AddressModel am = new AddressModel(this.cluster);

                LinkedList<AddressBean> addressList = am.getAddressesForUser(username);
                request.setAttribute("addressList", addressList);

                request.getSession().setAttribute("Profile", profile);

                CommentModel cm = new CommentModel(this.cluster);

                LinkedList<CommentBean> commentList = cm.getCommentsForThread(profile.getUserID());
                request.setAttribute("comments", commentList);

                RequestDispatcher rd = request.getRequestDispatcher("/profile.jsp");

                try {
                    rd.forward(request, response);
                } catch(ServletException | IOException e) {
                    if(Constants.VERBOSE) {
                        System.out.println("Error getting user profile data (forwarding).");
                    }
                    if(Constants.DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        } catch(NullPointerException e) {
            if(Constants.DEBUG) {
                System.out.println("---- Error at ServletProfile#doGet method ----\n\n");
                e.printStackTrace();
            }
            throw new NullPointerException("Cassandra cluster unavailable.");
        }
    }

}
