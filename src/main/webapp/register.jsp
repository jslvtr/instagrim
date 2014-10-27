<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
                        <li class="active"><a href="/instagrim-js/register.jsp">Register</a></li>
                        <li><a href="/instagrim-js/login.jsp">Login</a></li>
                    </ul>
                </div>
            </div>
        </nav>
       
        <article>
            <h1>Instagrim <small>Register as user</small></h1>
            <br/>
            <form method="POST" action="Register" role="form" class="form-horizontal">
                <div class="form-group">
                    <div class="col-xs-4">
                        <label for="inputUsername" class="col-sm-2 control-label">Email</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="inputUsername" name="username">
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-xs-4">
                        <label for="inputPassword" class="col-sm-2 control-label">Password</label>
                        <div class="col-sm-10">
                            <input type="password" class="form-control" id="inputPassword" name="password">
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="submit" class="btn btn-default">Sign in</button>
                    </div>
                </div>
            </form>

        </article>
    </body>
</html>
