<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn" %>
<%--
  Created by IntelliJ IDEA.
  User: jslvtr
  Date: 19/10/2014
  Time: 19:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit your profile</title>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.css" />
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-theme.css" />
    <script src="/js/jquery-1.11.1.min.js"></script>
    <script src="/js/bootstrap.min.js"></script>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>

<%
    LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
    String username = "";
    if(lg != null) {
        username = lg.getUsername();
    }
    String profileContent = (String)session.getAttribute("profile_content");
%>

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
        </div>
    </div>
</nav>

<h1>Edit your profile</h1>

<%
    if(lg != null && lg.getLoggedIn()) {
%>
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
                    <option value="Grayscale">Grayscale</option>
                </select>
            </div>
        </div>
    </div>
    <input type="hidden" name="picid" value="<%=lg.getUserID()%>" />
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-default">Upload</button>
        </div>
    </div>
</form>
<%
    }
%>

<div>
<form method="POST" action="profile">
    <label for="content">Content</label>
    <textarea id="content" name="content" rows="15" cols="30">
        <%
            if (profileContent != null && !profileContent.equals("")) {
        %>
        <%=profileContent%>
        <%
            } else {
        %>
        Enter your new profile details here.
        Everyone will be able to see!
        <%
            }
        %>
    </textarea>
    <div class="form-group">
        <div class="col-xs-4">
            <label for="name" class="col-sm-2 control-label">New address friendly name</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="name" name="name" value="">
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="col-xs-4">
            <label for="city" class="col-sm-2 control-label">City</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="city" name="city" value="">
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="col-xs-4">
            <label for="street" class="col-sm-2 control-label">Street</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="street" name="street" value="">
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="col-xs-4">
            <label for="zip" class="col-sm-2 control-label">ZIP</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" id="zip" name="zip" value="">
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-default">Submit</button>
        </div>
    </div>
</form>
</div>
</body>
</html>
