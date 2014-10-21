<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn" %>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.Profile" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Profile page</title>
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
    Profile profile = (Profile)session.getAttribute("Profile");
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
                <li><a href="upload.jsp">Upload</a></li>

                <%
                    if (lg != null && lg.getLoggedIn()) {
                %>

                <li><a href="/Images/<%=lg.getUsername()%>">Your Images</a></li>
                <li><a href="/profile/<%=lg.getUsername()%>">Your Profile</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Profile <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="#">View your profile</a></li>
                        <li><a href="#">Edit your profile</a></li>
                    </ul>
                </li>

                <%
                    } else {
                %>
                <li><a href="register.jsp">Register</a></li>
                <li><a href="login.jsp">Login</a></li>
                <%
                    }
                %>


            </ul>
        </div>
    </div>
</nav>

<article>

    <%
        if(profile != null) {
            if(profile.getUsername() != null && !profile.getUsername().equals("")) {
    %>
    <h1><%=profile.getUsername()%>'s profile</h1>
    <%
            }
            if(profile.getContent() != null && !profile.getContent().equals("")) {
                for(String line : profile.getContent().split("\r\n")) {
    %>


    <p><%=line%></p>
    <%
                }
            } else {
    %>
    <p>There's nothing here yet!</p>
    <%
            }
    %>
    <%
        } else {
    %>
    <h1>Profile doesn't exist!</h1>
    <%
        }
    %>
</article>

</body>
</html>
