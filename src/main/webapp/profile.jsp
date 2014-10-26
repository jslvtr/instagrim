<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn" %>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.Profile" %>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.CommentBean" %>
<%@ page import="java.util.UUID" %>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.AddressBean" %>
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
    java.util.LinkedList<CommentBean> commentList = (java.util.LinkedList<CommentBean>)request.getAttribute("comments");
    java.util.LinkedList<AddressBean> addressList = (java.util.LinkedList<AddressBean>)request.getAttribute("addressList");
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

<article>

    <%
        if(profile != null) {
            if(profile.getUsername() != null && !profile.getUsername().equals("")) {
    %>
    <h1><%=profile.getUsername()%>'s profile</h1>
    <a href="/FullImage/<%=profile.getUserID()%>"><img src="/Thumb/<%=profile.getUserID()%>"></a><br/>
    <a href="/Image/<%=profile.getUserID()%>/delete">Delete</a><br/>
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
<h2>Address list</h2>
    <%
        if(addressList != null) {
            java.util.Iterator<AddressBean> iterator;
            iterator = addressList.iterator();
            while(iterator.hasNext()) {
                AddressBean address = iterator.next();

    %>

    <h3><%=address.getName()%></h3>
    <p><%=address.getCity()%></p>
    <p><%=address.getStreet()%></p>
    <p><%=address.getZip()%></p>

    <%

            }
        } else {
    %>
    <p>None yet!</p>
<h2>Comments</h2>
    <%
        }
        if(commentList != null) {
            java.util.Iterator<CommentBean> iterator;
            iterator = commentList.iterator();
            while(iterator.hasNext()) {
                CommentBean comment = iterator.next();

    %>

    <h3><%=comment.getUser()%></h3>
    <p><%=comment.getContent()%></p>
    <p><%=comment.getDate()%></p>

    <%

            }
        } else {
    %>
    <p>Be the first to comment!</p>
    <%
        }
    %>
    <form method="POST" action="/comment" role="form" class="form-horizontal">
        <div class="form-group">
            <div class="col-xs-4">
                <label for="commentContent" class="col-sm-2 control-label">Comment</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="commentContent" name="content">
                </div>
            </div>
        </div>
        <%
            if (lg != null && lg.getLoggedIn()) {
        %>
        <input type="hidden" name="threadID" value="<%=lg.getUserID()%>" />
        <input type="hidden" name="viewID" value="/profile/" />
        <input type="hidden" name="redirectTo" value="<%=profile.getUsername()%>" />
        <%
            } else {
        %>
        <input type="hidden" name="threadID" value="<%=UUID.randomUUID()%>" />
        <input type="hidden" name="viewID" value="/" />
        <input type="hidden" name="viewID" value="" />
        <%
            }
        %>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button type="submit" class="btn btn-default">Comment!</button>
            </div>
        </div>
    </form>
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
