<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.motechproject.ghana.telco.domain.Subscription" %>
<script type="text/javascript" src="js/search-subscription.js"></script>
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
            <td>${subscription.subscriber.number}</td>
            <td>${subscription.programType.programName}</td>
            <td>${subscription.startWeekAndDay.week}</td>
            <td>${subscription.status}</td>
            <td><a href="#" onclick="javascript:new $.Enrollment().unRegister(${subscription.subscriber.number}, '${subscription.programType.programKey}')">Unregister</a></td>
            <c:if test="${subscription.programType.shortCodes[0] == 'P'}">
                <td><a href="#" onclick="javascript:new $.Enrollment().rollover(${subscription.subscriber.number})">Rollover</a></td>
            </c:if>
        </tr>
    </c:forEach>
</table>