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
        <span align="left" class="title">Welcome ${sessionScope.PRINCIPAL.username}</span>
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
                            <td><label>Program:</label></td>
                            <td>
                                <select id="program">
                                    <option value="P">Pregnancy</option>
                                    <option value="C">Childcare</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td><label>Start Time:</label></td>
                            <td>
                                <select id="startTimeP">
                                    <option value="5">5</option>
                                    <option value="6">6</option>
                                    <option value="7">7</option>
                                    <option value="8">8</option>
                                    <option value="9">9</option>
                                    <option value="10">10</option>
                                    <option value="11">11</option>
                                    <option value="12">12</option>
                                    <option value="13">13</option>
                                    <option value="14">14</option>
                                    <option value="15">15</option>
                                    <option value="16">16</option>
                                    <option value="17">17</option>
                                    <option value="18">18</option>
                                    <option value="19">19</option>
                                    <option value="20">20</option>
                                    <option value="21">21</option>
                                    <option value="22">22</option>
                                    <option value="23">23</option>
                                    <option value="24">24</option>
                                    <option value="25">25</option>
                                    <option value="26">26</option>
                                    <option value="27">27</option>
                                    <option value="28">28</option>
                                    <option value="29">29</option>
                                    <option value="30">30</option>
                                    <option value="31">31</option>
                                    <option value="32">32</option>
                                    <option value="33">33</option>
                                    <option value="34">34</option>
                                    <option value="35">35</option>
                                    <option value="36">36</option>
                                    <option value="37">37</option>
                                    <option value="38">38</option>
                                    <option value="39">39</option>
                                    <option value="40">40</option>
                                    <option value="41">41</option>
                                    <option value="42">42</option>
                                    <option value="43">43</option>
                                    <option value="44">44</option>
                                    <option value="45">45</option>
                                    <option value="46">46</option>
                                    <option value="47">47</option>
                                    <option value="48">48</option>
                                    <option value="49">49</option>
                                    <option value="50">50</option>
                                    <option value="51">51</option>
                                    <option value="52">52</option>
                                </select>
                                <select id="startTimeC">
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                    <option value="6">6</option>
                                    <option value="7">7</option>
                                    <option value="8">8</option>
                                    <option value="9">9</option>
                                    <option value="10">10</option>
                                    <option value="11">11</option>
                                    <option value="12">12</option>
                                </select>
                            </td>
                        </tr>
                        <tr></tr>
                        <tr></tr>
                        <tr></tr>
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
