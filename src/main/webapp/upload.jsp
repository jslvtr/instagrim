<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn" %>
<%--
    Document   : upload
    Created on : Sep 22, 2014, 6:31:50 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
                        <li class="active"><a href="/upload.jsp">Upload</a></li>

                        <%
                            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                            if (lg != null && lg.getLoggedIn()) {
                        %>

                        <li><a href="/Images/<%=lg.getUsername()%>">Your Images</a></li>
                        <li><a href="/profile/<%=lg.getUsername()%>">Your Profile</a></li>
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
                </div>
            </div>
        </nav>
 
        <article>
            <h1>Instagrim <small>File Upload</small></h1>
            <form method="POST" enctype="multipart/form-data" action="Image">
                <div class="form-group">
                    <div class="col-xs-4">
                        <label for="uploadFile" class="col-sm-2 control-label">File to upload</label>
                        <div class="col-sm-10">
                            <input type="file" class="form-control" id="uploadFile" name="upfile">
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-xs-4">
                        <label for="filter" class="col-sm-2 control-label">Filter to apply</label>
                        <div class="col-sm-10">
                            <select id="filter" name="filter">
                                <option value="Invert">Invert</option>
                                <option value="GainLight">GainLight</option>
                                <option value="GainDark">GainDark</option>
                                <option value="Pointillize">Pointillize</option>
                                <option value="Grayscale">Grayscale</option>
                            </select>
                        </div>
                    </div>
                </div>
                <input type="hidden" name="picid" value="" />
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="submit" class="btn btn-default">Upload</button>
                    </div>
                </div>
            </form>

        </article>
    </body>
</html>
