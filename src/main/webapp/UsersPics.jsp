<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/instagrim-js/css/bootstrap.css" />
        <link rel="stylesheet" type="text/css" href="/instagrim-js/css/bootstrap-theme.css" />
        <script src="/instagrim-js/js/jquery-1.11.1.min.js"></script>
        <script src="/instagrim-js/js/bootstrap.min.js"></script>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
    </head>
    <body>

    <nav class="navbar navbar-default" role="navigation">
        <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/instagrim-js/">instagrim</a>
            </div>

            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                    <li><a href="/instagrim-js/upload.jsp">Upload</a></li>

                    <%
                        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                        if (lg != null && lg.getLoggedIn()) {
                    %>

                    <li><a href="/instagrim-js/Images/<%=lg.getUsername()%>">Your Images</a></li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Profile <span class="caret"></span></a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="/instagrim-js/profile/<%=lg.getUsername()%>">View your profile</a></li>
                            <li><a href="/instagrim-js/edit_profile.jsp">Edit your profile</a></li>
                        </ul>
                    </li>

                    <%
                    } else {
                    %>
                    <li><a href="/instagrim-js/register.jsp">Register</a></li>
                    <li><a href="/instagrim-js/login.jsp">Login</a></li>
                    <%
                        }
                    %>


                </ul>
            </div>
        </div>
    </nav>

    <article>
        <h1>Your Pics</h1>
        <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>)request.getAttribute("Pics");
            if(lsPics == null) {
        %>
        <p>No Pictures found</p>
        <%
        } else {
            Iterator<Pic> iterator;
            iterator = lsPics.iterator();
            while(iterator.hasNext()) {
                Pic p = (Pic)iterator.next();

        %>
        <a href="/instagrim-js/Image/<%=p.getUUID()%>"><img src="/instagrim-js/Thumb/<%=p.getUUID()%>"></a><br/>
        <a href="/instagrim-js/Image/<%=p.getUUID()%>/delete">Delete</a><br/>
        <%

                }
            }
        %>
</html>
