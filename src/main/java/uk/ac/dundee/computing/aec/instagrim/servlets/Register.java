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

import uk.ac.dundee.computing.aec.instagrim.Constants;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.models.User;

/**
 * Class to function as a WebServlet that will allow users to register for our service.
 *
 * @author Administrator
 * @author jslvtr
 *
 * @since 16 Oct 2014
 */
@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {

    private Cluster cluster = null;

    public void init(ServletConfig config) throws ServletException {
        try {
            this.cluster = CassandraHosts.getCluster();
        } catch(Exception e) {
            if(Constants.DEBUG) {
                System.out.println("---- Error at Register Servlet init ----\n\n");
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
        try {

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            User us = new User();
            us.setCluster(cluster);
            us.RegisterUser(username, password);
        } catch(Exception e) {
            if(Constants.DEBUG) {
                System.out.println("---- Error at Register doPost user creation ----\n\n");
                e.printStackTrace();
            }
        }

        try {
            response.sendRedirect("/");
        } catch(IOException ioe) {
            if(Constants.DEBUG) {
                System.out.println("---- Error at Register doPost redirect ----\n\n");
                ioe.printStackTrace();
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
