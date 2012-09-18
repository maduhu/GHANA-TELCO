<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>
    <title>Ghana TELCO</title>

    <script type="text/javascript" src="js/lib/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="js/lib/jquery-ui-1.8.16.custom.min.js"></script>
    <script type="text/javascript" src="js/lib/formly.min.js"><!-- required for FF3 and Opera --></script>

    <link rel="stylesheet" type="text/css" href="css/application.css" media="screen, projection, print"/>
    <link rel="stylesheet" type="text/css" href="css/lib/jquery-ui-1.8.16.custom.css"
          media="screen, projection, print"/>
    <link rel="stylesheet" type="text/css" media="screen" href="css/lib/formly.min.css"/>

    <script type="text/javascript">
        $(document).ready(function () {
            $("form").formly({'onBlur':false, 'theme':'Light'});
        });
    </script>
</head>
<body>
<div class="content">

    <div class="login">
        <div id="page-wrap">

            <div class="logo_center">
                <img src="images/motech.jpeg"/>
            </div>

            <c:if test="${not empty param.login_error}">
                <div class="errorblock">Your login attempt was not successful, try again.</div>
            </c:if>

            <form name="f" action="${pageContext.servletContext.contextPath}/j_spring_security_check" method="POST">
                <label for="j_username">Username</label>
                <input type="text" name="j_username" id="j_username"/>
                <br/>
                <label for="j_password">Password</label>
                <input type="password" name="j_password" id="j_password" style="margin-left:0.5%"/>

                <br/>
                <input type="submit" value="Login"/>
                <br/>

            </form>
        </div>
    </div>
</div>
</body>
