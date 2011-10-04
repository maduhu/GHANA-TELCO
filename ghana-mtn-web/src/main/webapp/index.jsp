<html>
<head>
    <title>Mockup for MoTeCH</title>
    <script type="text/javascript" src="js/subscription.js"></script>
</head>
<body>
    </br>
    <form id="sms-form">
        <div style="width:50%; height: 90%; clear: right; float: left;">
            <div>Mobile Number : <input name="subscriberNumber" type="text" style="width: 30%" value="9512395123"/></div>
            <div style="">
                SMS : <input id="smsText" type="text" style="width: 40%" /> (eg. P 27, C 5)
                <input type="submit" onclick="return submitRequest(); return false;" value="Submit Request" style="width: 12%"/>
            </div>
            </br>
            <div id="requestResponse" style="height: 70%; width: 80%; border: 1px solid;"></div>
        </div>
    </form>
    <form id="event-form">
        <div style="width:50%; height: 90%; float: right;">
            <div style="">Mobile Number : <input name="subscriberNumberForEvent" type="text" style="width: 30%" value="9512395123"/></div>
            <div style="">
                Program :
                <select>
                    <option value="Pregnancy">Pregnancy</option>
                    <option value="Child Care">Child Care</option>
                </select>
                <input type="submit" onclick="return submitEventRequest(); return false;" value="Send Event" style="width: 12%"/>
            </div>
            </br>
            <div id="triggerResponse" style="height: 70%; width: 100%; border: 1px solid;"></div>
        </div>
    </form>
</body>
</html>
