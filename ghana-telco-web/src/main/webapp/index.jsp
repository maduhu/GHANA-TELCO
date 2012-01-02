<html>
<head>
    <title>Ghana TELCO</title>
    <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
    <script type="text/javascript" src="js/new-subscription.js"></script>
    <link rel="stylesheet" type="text/css" href="css/subscription.css" media="screen, projection, print"/>
    <link rel="stylesheet" type="text/css" href="css/jquery-ui-1.8.16.custom.css" media="screen, projection, print"/>
</head>
<body>
<div class="title_box">
    <img src="images/mobile.png" class="icon"/>
    <span>Ghana TELCO Test Screen</span>
</div>
<div id="tabs">
    <ul>
        <li><a id="tab1" href="#tabs-1">Input</a></li>       

    </ul>
    <div id="tabs-1">
        <div class="enrollment_box">
            <form id="sms-form">
                <table>
                    <tr>
                        <td><label for="subNo">Mobile Number:</label></td>
                        <td><input id="subNo" name="subscriberNumber" type="text" value="9500012345"/></td>
                    </tr>
                    <tr>
                        <td><label for="smsText">SMS (eg. P 27, C 5):</label></td>
                        <td><input id="smsText" type="text"/></td>
                    </tr>
                    <tr>
                        <td><input id="submit_enrollment" type="button" value="Send"/></td>
                    </tr>
                </table>
            </form>
        </div>
        <div class="audit_box">
            <form id="audit-form">
                Audits :
                <select id="audit_options">
                    <option value="sms">SMSAudit</option>
                </select>
                <input id="refresh_audit" type="button" value="Refresh"/>
            </form>
            <div id="audit_table"></div>
        </div>
    </div>
    <div id="tabs-2">

    </div>
</div>
</body>
</html>
