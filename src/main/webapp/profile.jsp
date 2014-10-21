<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn" %>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.Profile" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Profile page</title>
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

<nav>
    <ul>
            <li><a href="upload.jsp">Upload</a></li>
        <%
            if(lg != null && lg.getLoggedIn()) {
        %>

            <li><a href="/Images/<%=username%>">Your Images</a></li>
        <%
            } else {
        %>
            <li><a href="register.jsp">Register</a></li>
            <li><a href="login.jsp">Login</a></li>
        <%
            }
        %>
    </ul>
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
    <h1>Unknown profile</h1>
    <p>There's nothing here yet!</p>
    <%
        }
    %>
</article>


<footer>
    <ul>
        <li class="footer"><a href="/">Home</a></li>
    </ul>
</footer>
</body>
</html>
