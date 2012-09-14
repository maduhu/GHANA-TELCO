<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table id="resultsTable">
    <tr>
        <th>Subscriber Number</th>
        <th>Program</th>
        <th>Week</th>
        <th>Status</th>
    </tr>
    <c:forEach var="subscription" items="${subscriptions}">
        <tr>
            <!--<td><input type="checkbox" name="checkbox_${subscription.programType.shortCodes[0]}" value="${subscription.subscriber.number}/${subscription.programType.shortCodes[0]}" /></td>-->
            <td><a href="#"
                   onclick="enrollment.getAllAuditSmsForSubscriber(${subscription.subscriber.number})">${subscription.subscriber.number}</a>
            </td>
            <td>${subscription.programType.programName}</td>
            <td>${subscription.startWeekAndDay.week}</td>
            <td>${subscription.status}</td>
            <c:if test="${subscription.status == 'ACTIVE' || subscription.status == 'WAITING_FOR_ROLLOVER_RESPONSE'}">
                <td><a href="#"
                       onclick="javascript:enrollment.unRegister(${subscription.subscriber.number}, '${subscription.programType.programKey}')">Unregister</a>
                </td>
            </c:if>
            <c:if test="${subscription.programType.shortCodes[0] == 'P' && subscription.status == 'ACTIVE'}">
                <td><a href="#" onclick="javascript:enrollment.rollover(${subscription.subscriber.number})">Rollover</a>
                </td>
            </c:if>
        </tr>
    </c:forEach>
</table>