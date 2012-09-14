<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>Ghana TELCO</title>
    <script type="text/javascript" src="js/lib/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="js/lib/jquery-ui-1.8.16.custom.min.js"></script>
    <script type="text/javascript" src="js/new-subscription.js"></script>
    <link rel="stylesheet" type="text/css" href="css/application.css" media="screen, projection, print"/>
    <link rel="stylesheet" type="text/css" href="css/lib/jquery-ui-1.8.16.custom.css"
          media="screen, projection, print"/>
</head>
<body>
<div class="content">

    <div class="logo_center">
        <img src="images/motech.jpeg"/>
    </div>
    <div>
        <span align="left" class="title">Ghana Telco Admin</span>
        <span style="float:right"><a href="${pageContext.servletContext.contextPath}/logout">Logout</a></span><br>
        <span style="float:right"><a href="${pageContext.servletContext.contextPath}/password/page">Reset
            Password</a></span>
    </div>
    </br>
    <div id="tabs">
        <ul>
            <li><a id="tab1" href="#tabs-1">Logs/Registration</a></li>
            <li><a id="tab2" href="#tabs-2">Search</a></li>
        </ul>
        <div id="tabs-1">
            <div class="enrollment_box">
                <form id="sms-form">
                    <table>
                        <tr>
                            <td><label for="subNo">Mobile Number:</label></td>
                            <td><input id="subNo" name="subscriberNumber" type="text" value="" size="16"/></td>
                        </tr>
                        <tr>
                            <td><label for="smsText">SMS (eg. P 27, C 5):</label></td>
                            <td><input id="smsText" type="text" size="16"/></td>
                        </tr>
                        <tr>
                            <td></td>
                            <td><input id="submit_enrollment" type="button" value="Register"/></td>
                        </tr>
                    </table>
                </form>
            </div>
            <div class="audit_box">
                <form id="audit-form">
                    Audits :
                    <select id="audit_options">
                        <option value="sms/outbound">Outbound SMS</option>
                        <option value="sms/inbound">Inbound SMS</option>
                    </select>
                    <input id="refresh_audit" type="button" value="Refresh"/>
                </form>
                <div id="audit_table"></div>
            </div>
        </div>
        <div id="tabs-2">
            <div class="search_box">
                <form id="search-form">
                    <table border="1">
                        <tr>
                            <td valign="top"><b><label for="subNo"><u>Subscriber Number:</u></label></b></td>
                            <td valign="top">
                                <table>
                                    <tr>
                                        <td><input id="searchSubNo" name="subscriberNumber" type="text" size="16"/></td>
                                    </tr>
                                    <tr></tr>
                                    <tr></tr>
                                    <tr>
                                        <td valign="bottom"><input id="search_enrollment" type="button" value="Search"/>
                                        </td>
                                    </tr>
                                </table>
                            <td valign="top">
                                <div id="programName">
                                    <table>
                                        <tr>
                                            <td valign="top" align="left" style="width:10%">
                                                <b><label><u>Program:</u></label></b></td>
                                        </tr>
                                        <tr>
                                            <td><input name="programName" type="checkbox" value="PREGNANCY"
                                                       checked="checked"/>Pregnancy
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><input name="programName" type="checkbox" value="CHILDCARE"
                                                       checked="checked"/>Childcare
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </td>

                            <td valign="top">
                                <div id="status">
                                    <table align="left">
                                        <tr>
                                            <td valign="top" align="left"><b><label><u>Status:</u></label></b></td>
                                        </tr>
                                        <tr>
                                            <td><input name="status" type="checkbox" value="ACTIVE" checked="checked"/>Active
                                            </td>
                                            <td><input name="status" type="checkbox" value="ROLLED_OFF"/>Rolled_off</td>
                                        </tr>
                                        <tr>
                                            <td><input name="status" type="checkbox" value="SUSPENDED"/>Suspended</td>
                                            <td><input name="status" type="checkbox"
                                                       value="WAITING_FOR_ROLLOVER_RESPONSE"/>Waiting_for_rollover
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
            <div id="result_table" class="search_result"></div>
            <div class="audit_box2">
                <div id="sms_table"></div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
