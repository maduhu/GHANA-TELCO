<html>
<head>
    <title>Ghana MTN</title>
    <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
    <script type="text/javascript" src="js/new-subscription.js"></script>
    <%--<script>--%>
        <%--$(function() {--%>
            <%--$("#tabs").tabs();--%>
        <%--});--%>
    <%--</script>--%>
    <link rel="stylesheet" type="text/css" href="css/subscription.css" media="screen, projection, print"/>
    <link rel="stylesheet" type="text/css" href="css/jquery-ui-1.8.16.custom.css" media="screen, projection, print"/>
</head>
<body>
<div class="title_box">
    <img src="images/mobile.png" class="icon"/>
    <span>Ghana MTN Test Screen</span>
</div>
<div id="tabs">
    <ul>
        <li><a href="#tabs-1">Input</a></li>
        <li><a href="#tabs-2">Audits</a></li>
        <li><a href="#tabs-3">MTN</a></li>
    </ul>
    <div id="tabs-1">
        <div class="enrollment_box">
            <fieldset>
                <legend>Enrollment of subscribers</legend>
                <form id="sms-form">
                    <label for="subNo">Mobile Number:</label>
                    <input id="subNo" name="subscriberNumber" type="text" value="9500012345"/>
                    <label for="smsText">SMS (eg. P 27, C 5):</label>
                    <input id="smsText" type="text"/>
                    <input id="submit_enrollment" type="submit" value="Submit Request"/>
                </form>
            </fieldset>
        </div>
    </div>
    <div id="tabs-2">
        <div class="audit_box">
            <fieldset>
                <legend>Message Audit</legend>
                <form id="audit-form">
                    <select id="audit_options">
                        <option value="sms">SMSAudit</option>
                        <option value="bill">BillAudit</option>
                    </select>
                    <input id="submit_audit" type="submit" value="Refresh"/>
                </form>
                <div id="audit_table"></div>
            </fieldset>
        </div>
    </div>
    <div id="tabs-3">
        <div class="mtn_user_box">
            <fieldset>
                <legend>MTN Users</legend>
                <form id="mtn-form">
                    <input id="submit_mtn" type="submit" value="Refresh"/>
                </form>
                <div id="mtn_table"></div>
            </fieldset>
        </div>
    </div>
</div>
</body>
</html>
