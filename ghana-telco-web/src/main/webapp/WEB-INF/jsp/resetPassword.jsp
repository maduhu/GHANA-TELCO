<html>
<head>
    <title>Ghana TELCO</title>
    <script type="text/javascript" src="../js/lib/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="../js/lib/jquery-ui-1.8.16.custom.min.js"></script>
    <link rel="stylesheet" type="text/css" href="../css/application.css" media="screen, projection, print"/>
    <link rel="stylesheet" type="text/css" href="../css/lib/jquery-ui-1.8.16.custom.css" media="screen, projection, print"/>
    <script type="text/javascript" src="../js/reset-password.js"></script>
</head>
<body>
<div class="content">

    <div class="logo_center">
        <img src="../images/motech.jpeg"/>
    </div>
    <div>
        <span align="left" class="title">Ghana Telco Admin</span>
        <span style="float:right"><a href="${pageContext.servletContext.contextPath}/logout">Logout</a></span>
    </div>
    <div id="message">${requestScope.status}</div>
    <br><br>
        <div>
        <form action="reset" method="post" onsubmit="return validateForm()" name="passwordReset" >
            <table>
                <tr>
                    <td><label id="lblOldPassword" for="oldPassword">Old Password</label></td> <td><input type="password" name="oldPassword" id="oldPassword" /></td>
                </tr>
                <tr>
                    <td><label id="lblNewPassword" for="newPassword">New Password</label></td><td><input type="password" id="newPassword" name="newPassword" /></td>
                </tr>
                <tr>
                    <td><label id="lblRetypedPassword" for="retypedPassword">Retyped Password</label></td><td><input type="password" id="retypedPassword" name="retypedPassword" /></td>
                </tr>
                <tr><td colspan="2" align="center"><input type="submit" value="Reset" onclick=""></td></tr>
            </table>
        </form>
    </div>
    </br>
</div>
</body>
</html>