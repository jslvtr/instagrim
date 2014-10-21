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

<h1>Edit your profile</h1>
<form method="POST" action="profile">
    <label for="content">Content</label>
    <textarea user_id="content" name="content" rows="15" cols="30">
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
    <input type="submit" value="Register">
</form>

</body>
</html>
