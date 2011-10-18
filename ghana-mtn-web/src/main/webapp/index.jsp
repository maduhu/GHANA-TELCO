<html>
<head>
    <title>Ghana MTN</title>
    <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
    <script type="text/javascript" src="js/new-subscription.js"></script>
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
        <li><a id="tab1" href="#tabs-1">Input</a></li>
        <li><a id="tab2" href="#tabs-2">Audits</a></li>
        <li><a id="tab3" href="#tabs-3">MTN</a></li>
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
    </div>
    <div id="tabs-2">
        <div class="audit_box">
            <form id="audit-form">
                <select id="audit_options">
                    <option value="sms">SMSAudit</option>
                    <option value="bill">BillAudit</option>
                </select>
            </form>
            <div id="audit_table"></div>
        </div>
    </div>
    <div id="tabs-3">
        <div class="mtn_user_box">
            <div id="mtn_table_box"></div>
            <div id="mtn_user_edit">
                <form id="mtn_user_edit_form">
                    <table>
                        <tr>
                            <td><label for="mtn_user_no">Number:</label></td>
                            <td><input id="mtn_user_no" name="mtnUserNumber" type="text"/></td>
                        </tr>
                        <tr>
                            <td><label for="mtn_user_balance">Balance:</label></td>
                            <td><input id="mtn_user_balance" name="mtnUserBalance" type="text"/></td>
                        </tr>
                        <tr>
                            <td><input id="update_mtn_user" type="button" value="Add"></td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
