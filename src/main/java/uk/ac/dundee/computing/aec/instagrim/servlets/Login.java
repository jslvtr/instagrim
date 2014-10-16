/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.ac.dundee.computing.aec.instagrim.Constants;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

/**
 * Class to function as a WebServlet that will allow users to log into our service.
 *
 * @author Administrator
 * @author jslvtr
 *
 * @since 16 Oct 2014
 */
@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {

    private Cluster cluster = null;


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
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User us = new User();
        us.setCluster(cluster);
        boolean isValid = false;
        try {
            isValid = us.IsValidUser(username, password);
        } catch (NullPointerException se) {
            try {
                response.setStatus(500);
                response.sendError(500, "Database currently unavailable. Please try again later.");
                //response.sendRedirect("/login.jsp");
                return;
            } catch(IOException ioe) {
                if(Constants.DEBUG) {
                    System.out.println("---- Error at Login doPost 401 sendError ----\n\n");
                    ioe.printStackTrace();
                }
            }
        }

        HttpSession session = request.getSession();
        if(Constants.VERBOSE) {
            System.out.println("Session in servlet " + session);
        }

        if(isValid) {
            LoggedIn lg = new LoggedIn();
            lg.setLogedin();
            lg.setUsername(username);
            //request.setAttribute("LoggedIn", lg);

            session.setAttribute("LoggedIn", lg);

            if(Constants.VERBOSE) {
                System.out.println("Session in servlet " + session);
            }

            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            try {
                rd.forward(request, response);
            } catch(ServletException | IOException e) {
                if(Constants.DEBUG) {
                    System.out.println("---- Error at Login doPost forward ----\n\n");
                    e.printStackTrace();
                }
            }

        } else {
            try {
                response.setStatus(401);
                response.sendError(401, "Wrong username or password.");
                //response.sendRedirect("/login.jsp");
            } catch(IOException ioe) {
                if(Constants.DEBUG) {
                    System.out.println("---- Error at Login doPost redirect ----\n\n");
                    ioe.printStackTrace();
                }
            }

        }

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
