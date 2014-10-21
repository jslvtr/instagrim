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
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
        </header>
        <nav>
            <ul>

               
                <li><a href="upload.jsp">Upload</a></li>
                    <%
                        
                        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                        if (lg != null && lg.getLoggedIn()) {
                    %>

                <li><a href="/Images/<%=lg.getUsername()%>">Your Images</a></li>
                <li><a href="/profile/<%=lg.getUsername()%>">Your Profile</a></li>
                <li><a href="edit_profile.jsp">Edit your profile</a></li>
                <%
                            }else{
                                %>
                 <li><a href="register.jsp">Register</a></li>
                <li><a href="login.jsp">Login</a></li>
                <%
                                        
                            
                    }%>
            </ul>
        </nav>
        <footer>
            <ul>
                <li class="footer"><a href="/">Home</a></li>
                <li>&COPY; Andy C</li>
            </ul>
        </footer>
    </body>
</html>
