<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id='server_time'>${time}</div>
<table>
    <tr>
        <th>Subscriber</th>
        <th>Sent on</th>
        <th>Content</th>
    </tr>
    <c:forEach var="smsRecord" items="${smsRecords}">
        <tr>
            <td><a href="#" onclick="enrollment.getAuditForSubscriber(${smsRecord.phoneNo})">${smsRecord.phoneNo}</a></td>
            <td>${smsRecord.messageTime}</td>
            <td>${smsRecord.content}</td>
        </tr>
    </c:forEach>
</table>