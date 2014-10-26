package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.*;

import uk.ac.dundee.computing.aec.instagrim.Constants;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Converters;
import uk.ac.dundee.computing.aec.instagrim.models.CommentModel;
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
import java.util.LinkedList;

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
        if(Constants.VERBOSE) {
            System.out.println("New profile content: " + content);
            String[] contentArray = content.split("\r\n");
            for(String line : contentArray) {
                System.out.println(line);
            }
        }

        LoggedIn lg = (LoggedIn)request.getSession().getAttribute("LoggedIn");
        String username = lg.getUsername();

        String updateQuery = "UPDATE userprofiles SET profile_content = ? WHERE login = ?";

        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare(updateQuery);
        BoundStatement boundStatement = new BoundStatement(ps);

        session.execute(boundStatement.bind(content, username));

        session.close();
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
            username = args[1];
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
            Session session = cluster.connect("instagrim");
            PreparedStatement ps = session.prepare("SELECT profile_content FROM userprofiles WHERE login = ? LIMIT 1");
            ResultSet rs;
            BoundStatement boundStatement = new BoundStatement(ps);

            rs = session.execute(boundStatement.bind(username));

            if(rs.isExhausted()) {
                System.out.println("No Images returned");
            } else {
                Profile profile = new Profile();
                profile.setContent(rs.one().getString("profile_content"));
                profile.setUsername(username);
                request.getSession().setAttribute("Profile", profile);

                CommentModel cm = new CommentModel(this.cluster);

                LinkedList<CommentBean> commentList = cm.getCommentsForThread(lg.getUserID());
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
