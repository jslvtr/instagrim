<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/css/bootstrap.css" />
        <link rel="stylesheet" type="text/css" href="/css/bootstrap-theme.css" />
        <script src="/js/jquery-1.11.1.min.js"></script>
        <script src="/js/bootstrap.min.js"></script>

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
                    <a class="navbar-brand" href="/">instagrim</a>
                </div>

                <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                    <ul class="nav navbar-nav">
                        <li><a href="/upload.jsp">Upload</a></li>

                        <%
                            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                            if (lg != null && lg.getLoggedIn()) {
                        %>

                        <li><a href="/Images/<%=lg.getUsername()%>">Your Images</a></li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Profile <span class="caret"></span></a>
                            <ul class="dropdown-menu" role="menu">
                                <li><a href="/profile/<%=lg.getUsername()%>">View your profile</a></li>
                                <li><a href="/edit_profile.jsp">Edit your profile</a></li>
                            </ul>
                        </li>

                        <%
                            } else {
                        %>
                        <li><a href="/register.jsp">Register</a></li>
                        <li><a href="/login.jsp">Login</a></li>
                        <%
                            }
                        %>


                    </ul>

                    <div class="navbar-form navbar-left" role="search"">
                        <div class="form-group">
                            <input type="text" id="profile_search" class="form-control" placeholder="Search for profile">
                        </div>
                        <button type="submit" class="btn btn-default" onClick="javascript:window.location.href='/profile/'.concat(document.getElementById('profile_search').value   );">Search</button>
                    </div>
                </div>
            </div>
        </nav>
    </body>
</html>
