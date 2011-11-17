package org.motechproject.ghana.mtn.controller;

import org.drools.core.util.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.repository.AllBillAudits;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.SMSAudit;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.repository.AllSMSAudits;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static java.util.Arrays.asList;

@Controller
public class AuditController {
    @Autowired
    private AllSMSAudits allProgramMessageAudits;
    @Autowired
    private AllBillAudits allBillAudits;
    @Autowired
    private AllSubscriptions allSubscriptions;

    @RequestMapping("/audits/sms")
    public void showAllSMSAudits(HttpServletResponse response) throws IOException {
        List<SMSAudit> messageAudits = sort(allProgramMessageAudits.getAll(), on(SMSAudit.class).getSentTime(), sortComparator());

        StringBuilder builder = new StringBuilder();
        builder.append("<div id='server_time'>" + DateUtil.now() + "</div>");
        builder.append("<table>");
        row(builder, header("Subscriber", "Program", "Sent on", "Content"));
        for (SMSAudit messageAudit : messageAudits) {
            List<? extends Object> dataList = asList(messageAudit.getSubscriberNumber(), messageAudit.getProgramKey(), messageAudit.getSentTime().toString(),
                    messageAudit.getContent());
            rowData(builder, dataList);
        }

        builder.append("</table>");
        response.getWriter().write(builder.toString());
    }

    @RequestMapping("/audits/bill")
    public void showAllBillAudits(HttpServletResponse response) throws IOException {
        List<BillAudit> billAudits = sort(allBillAudits.getAll(), on(BillAudit.class).getDate(), sortComparator());

        StringBuilder builder = new StringBuilder();
        builder.append("<div id='server_time'>" + DateUtil.now() + "</div>");
        builder.append("<table>");
        row(builder, header("Subscriber", "Program", "Date", "Status", "Amount Charged", "Failure Reason"));
        for (BillAudit billAudit : billAudits) {
            rowData(builder, asList(billAudit.getMobileNumber(), billAudit.getProgram(), billAudit.getDate(), billAudit.getBillStatus()
                            , billAudit.getAmountCharged(), billAudit.getFailureReason()));
        }

        builder.append("</table>");
        response.getWriter().write(builder.toString());
    }

    @RequestMapping("/audits/bill/schedule")
    public void showBillAuditsAsSchedule(HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        title(writer, IProgramType.PREGNANCY);
        writer.write(getBillAuditsForSubscriptions(allSubscriptions.getAllActiveSubscriptions(IProgramType.PREGNANCY)));
        title(writer, IProgramType.CHILDCARE);
        writer.write(getBillAuditsForSubscriptions(allSubscriptions.getAllActiveSubscriptions(IProgramType.CHILDCARE)));
    }

    private Comparator<DateTime> sortComparator() {
        return new Comparator<DateTime>() {
            @Override
            public int compare(DateTime o1, DateTime o2) {
                return o2.compareTo(o1);
            }
        };
    }

    private void title(PrintWriter writer, String programName) {
        writer.write("<b>" + programName + " Program Bill Audits</b>");
    }

    private String getBillAuditsForSubscriptions(List<Subscription> allPregnancySubscriptions) {
        StringBuilder billAuditsTable = new StringBuilder();
        billAuditsTable.append("<table border='1'>");
        for (Subscription pregnancySubscription : allPregnancySubscriptions) {
            billAuditsTable.append("<tr>")
                    .append("<td style=\"width: 100px;\">" + pregnancySubscription.subscriberNumber())
                    .append("<div class=\"subscriptionStatus\">[" + pregnancySubscription.getStatus() + "]</div>")
                    .append("</td>");

            List<BillAudit> billAudits = allBillAudits.fetchAuditsFor(pregnancySubscription.subscriberNumber(), pregnancySubscription.programKey());

            for (BillAudit billAudit : billAudits) {
                String color = StringUtils.isEmpty(billAudit.getFailureReason()) ? "green" : "red";
                billAuditsTable.append("<td style=\"width: 100px;color: " + color + "\">" + billAudit.getDate().toDate() + "</td>");
            }
            billAuditsTable.append("</tr>");
        }
        billAuditsTable.append("</table>");
        return billAuditsTable.toString();
    }

    private AuditController row(StringBuilder buffer, String data) {
        buffer.append("<tr>").append(data).append("</tr>");
        return this;
    }

    private AuditController rowData(StringBuilder buffer, List<? extends Object> dataList) {
        buffer.append("<tr>");
        for(Object data : dataList) buffer.append("<td>").append(data).append("</td>");
        buffer.append("</tr>");
        return this;
    }

    private String header(Object... headers) {
         StringBuilder builder = new StringBuilder();
         for(Object header : headers) {
             builder.append("<th>").append(header).append("</th>");
         }
        return builder.toString();
    }
}
