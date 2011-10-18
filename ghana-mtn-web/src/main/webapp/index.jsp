<html>
<head>
    <title>Ghana MTN</title>
    <script type="text/javascript" src="js/subscription.js"></script>
    <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
    <script>
        $(function() {
            $("#tabs").tabs();
        });
    </script>
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
        <li><a href="#tabs-1">Enrollment</a></li>
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
                    <input type="submit" onclick="return submitRequest(); return false;" value="Submit Request"/>
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
                    <input type="submit" onclick="return refreshAudit(); return false;" value="Refresh"/>
                </form>
                <div id="audit_table"></div>
            </fieldset>
        </div>
    </div>
    <div id="tabs-3">
        <div class="send_event_box">
            <fieldset>
                <legend>Publish event</legend>
                <form id="event-form">
                    <label for="subNoForEvent">Mobile Number:</label>
                    <input id="subNoForEvent" name="subscriberNumberForEvent" type="text" value="9512395123"/>
                    <label for="program_options">Program:</label>
                    <select id="program_options">
                        <option value="Pregnancy">Pregnancy</option>
                        <option value="Child Care">Child Care</option>
                    </select>
                    <input type="submit" onclick="return submitEventRequest(); return false;" value="Send Event"/>
                </form>
            </fieldset>
        </div>
    </div>
</div>


</body>
</html>
