<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="audit_box1">
    <div id="sms_audit_table">
        <table>
            <tr class="">
                <th>Subscriber</th>
                <th>Type</th>
                <th>Sent on</th>
                <th>Content</th>
            </tr>
            <c:forEach var="smsRecord" items="${smsRecords}">
                <tr>
                    <td>${smsRecord.phoneNo}</td>
                    <td>${smsRecord.type}</td>
                    <td>${smsRecord.messageTime}</td>
                    <td>${smsRecord.content}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>