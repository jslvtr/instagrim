package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.*;

import uk.ac.dundee.computing.aec.instagrim.Constants;
import uk.ac.dundee.computing.aec.instagrim.exceptions.FailureToPostException;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Converters;
import uk.ac.dundee.computing.aec.instagrim.models.CommentModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * URL Patterns: /comment/*, where * is the thread ID; or /comment to post a comment.
 */
@WebServlet(name = "Comment", urlPatterns = {"/comment"})
public class ServletComment extends HttpServlet {

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
                System.out.println("---- Error at Comment Servlet init ----\n\n");
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param request the request that was made as POST
     * @param response our response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String content = request.getParameter("content");
        UUID threadID = UUID.fromString(request.getParameter("threadID"));
        String redirectTo = request.getParameter("redirectTo");
        if(Constants.VERBOSE) {
            System.out.println("New comment: " + content);
            String[] contentArray = content.split("\r\n");
            for(String line : contentArray) {
                System.out.println(line);
            }
        }

        LoggedIn lg = (LoggedIn)request.getSession().getAttribute("LoggedIn");
        String username = lg.getUsername();

        CommentModel cm = new CommentModel(this.cluster);

        boolean success = cm.addComment(threadID, content, username);

        if(success) {
            try {
                response.sendRedirect(request.getParameter("viewID") + redirectTo);
            } catch(IOException e) {
                if(Constants.VERBOSE) {
                    System.out.println("Error forwarding after posting a comment!");
                }
                if(Constants.DEBUG) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                response.setStatus(500);
                response.sendError(500, "Failure to post comment. Sorry!");
                throw new FailureToPostException("Failure to post comment!");
            } catch(IOException ioe) {
                if(Constants.DEBUG) {
                    System.out.println("---- Error at ServletComment redirect ----\n\n");
                    ioe.printStackTrace();
                }
            } catch(FailureToPostException fe) {
                if(Constants.DEBUG) {
                    System.out.println("---- Error at ServletComment FailureToPostException ----\n\n");
                    fe.printStackTrace();
                }
            }
        }
    }

// To delete soon!



//    /**
//     *
//     * @param request
//     * @param response
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
//        CommentModel cm = new CommentModel(this.cluster);
//        UUID threadID = null;
//
//        String args[] = Converters.SplitRequestPath(request);
//
//        try {
//            threadID = UUID.fromString(args[1]);
//        } catch(NullPointerException|ArrayIndexOutOfBoundsException e) {
//            if(Constants.VERBOSE) {
//                System.out.println("Error getting comments for this thread.");
//                System.out.println("Maybe the URL was manually modified?");
//            }
//            if(Constants.DEBUG) {
//                e.printStackTrace();
//            }
//        }
//
//        LinkedList<CommentBean> commentList = cm.getCommentsForThread(threadID);
//
//        request.setAttribute("comments", commentList);
//
//        RequestDispatcher rd;
//        User user = new User();
//        user.setCluster(this.cluster);
//        String usernameForID = user.getUsernameForID(threadID);
//
//        if(usernameForID != null) {
//            rd = request.getRequestDispatcher("/profile/" + usernameForID);
//        } else {
//            rd = request.getRequestDispatcher("/Image/" + threadID);
//        }
//
//        try {
//            rd.forward(request, response);
//        } catch(ServletException | IOException e) {
//            if(Constants.VERBOSE) {
//                System.out.println("Error forwarding after posting a comment!");
//            }
//            if(Constants.DEBUG) {
//                e.printStackTrace();
//            }
//        }
//    }

}
