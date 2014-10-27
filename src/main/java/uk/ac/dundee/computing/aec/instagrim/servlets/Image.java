package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.jhlabs.image.InvertFilter;
import uk.ac.dundee.computing.aec.instagrim.Constants;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Converters;
import uk.ac.dundee.computing.aec.instagrim.models.CommentModel;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 * Class to function as a WebServlet that will allow users to view images and thumbnails in our service.
 *
 * @author Administrator
 * @author jslvtr
 *
 * @since 16 Oct 2014
 */
@WebServlet(urlPatterns = {
        "/Image",
        "/Image/*",
        "/Thumb/*",
        "/Images",
        "/Images/*",
        "/FullImage/*"
})
@MultipartConfig
public class Image extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;

    /**
     *
     */
    public Image() {
        super();
    }

    /**
     *
     * @param config
     */
    public void init(ServletConfig config) {
        try {
            this.cluster = CassandraHosts.getCluster();
        } catch (Exception e) {
            if(Constants.DEBUG) {
                System.out.println("---- Error at Image Servlet init ----\n\n");
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Converters.SplitRequestPath(request);

        try {
            if(args[1].equals("Image")) {
                try {
                    if(args[3] != null && args[3].toLowerCase().equals("delete")) {
                        DeleteImage(args[2], request, response);
                        return;
                    }
                } catch(ArrayIndexOutOfBoundsException ex) {
                    if(Constants.VERBOSE) {
                        System.out.println("Error accessing args[2]. Image is most likely not being deleted!");
                    }
                    if(Constants.DEBUG) {
                        ex.printStackTrace();
                    }
                }

                try { //show image with comments
                    DisplayImageWithComments(args[2], request, response);
                } catch (ArrayIndexOutOfBoundsException e) {
                    if(Constants.VERBOSE) {
                        System.out.println("Error accessing args[2]. Maybe no argument was provided?");
                    }
                    if(Constants.DEBUG) {
                        e.printStackTrace();
                    }
                }
            } else if(args[1].equals("Images")) {
                DisplayImageList(args[2], request, response);
            } else if(args[1].equals("Thumb")) {
                DisplayImage(Converters.DISPLAY_THUMB, args[2], response);
            } else if(args[1].equals("FullImage")) { //display image as raw
                try {
                    DisplayImage(Converters.DISPLAY_PROCESSED, args[2], response);
                } catch (ArrayIndexOutOfBoundsException e) {
                    if(Constants.VERBOSE) {
                        System.out.println("Error accessing args[1]. Maybe no argument was provided?");
                    }
                    if(Constants.DEBUG) {
                        e.printStackTrace();
                    }
                }
            } else {
                error("Bad Operator", response);
            }
        } catch (Exception e) {
            if(Constants.DEBUG) {
                e.printStackTrace();
            }

        }
    }

    private void DisplayImageWithComments(String arg, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestDispatcher rd = request.getRequestDispatcher("/image_comments.jsp");

        request.setAttribute("image", arg);
        CommentModel cm = new CommentModel(this.cluster);

        request.setAttribute("commentList", cm.getCommentsForThread(UUID.fromString(arg)));

        rd.forward(request, response);

    }

    /**
     *
     * @param uuid
     * @param request
     * @param response
     */
    private void DeleteImage(String uuid, HttpServletRequest request, HttpServletResponse response) {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);

        LoggedIn lg = (LoggedIn)request.getSession().getAttribute("LoggedIn");
        String username = lg.getUsername();
        if(lg.getLoggedIn()) {
            tm.deletePic(username, java.util.UUID.fromString(uuid));
        }

        RequestDispatcher rd = request.getRequestDispatcher("/Images/" + username);
        try {
            rd.forward(request, response);
        } catch (IOException | ServletException e) {
            if(Constants.VERBOSE) {
                System.out.println("Error forwarding after deleting image.");
            }
            if(Constants.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param User
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void DisplayImageList(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");

        PicModel tm = new PicModel();
        tm.setCluster(cluster);

        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User);
        request.setAttribute("Pics", lsPics);

        rd.forward(request, response);
    }

    /**
     *
     * @param type
     * @param Image
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void DisplayImage(int type, String Image, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);

        Pic p = tm.getPic(type, java.util.UUID.fromString(Image));

        OutputStream out = response.getOutputStream();

        response.setContentType(p.getType());
        response.setContentLength(p.getLength());

        InputStream is = new ByteArrayInputStream(p.getBytes());
        BufferedInputStream input = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];

        for(int length = 0; (length = input.read(buffer)) > 0; ) {
            out.write(buffer, 0, length);
        }

        out.close();
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String picid = request.getParameter("picid");
        String filterName = request.getParameter("filter");

        for(Part part : request.getParts()) {
            System.out.println("Part Name " + part.getName());
            if(part.getName().equals("upfile")) {

                String type = part.getContentType();
                String filename = part.getSubmittedFileName();
                String username = "majed";

                InputStream is = request.getPart(part.getName()).getInputStream();
                int i = is.available();

                HttpSession session = request.getSession();
                LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");

                if(lg.getLoggedIn()) {
                    username = lg.getUsername();
                }

                if(i > 0) {
                    byte[] b = new byte[i + 1];
                    is.read(b);
                    System.out.println("Length : " + b.length);
                    PicModel tm = new PicModel();
                    tm.setCluster(cluster);
                    tm.insertPic(b, type, filename, username, picid, filterName);

                    is.close();
                }
            }
        }
        RequestDispatcher rd = request.getRequestDispatcher("/upload.jsp");
        rd.forward(request, response);
    }

    /**
     *
     * @param message
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void error(String message, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = new PrintWriter(response.getOutputStream());

        out.println("<h1>You have an error in your input</h1>");
        out.println("<h2>" + message + "</h2>");

        out.close();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
