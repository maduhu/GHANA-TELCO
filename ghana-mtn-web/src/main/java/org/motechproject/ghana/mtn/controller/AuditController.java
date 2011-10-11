package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.repository.AllBillAudits;
import org.motechproject.ghana.mtn.domain.SMSAudit;
import org.motechproject.ghana.mtn.repository.AllSMSAudits;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

@Controller
public class AuditController {
    @Autowired
    private AllSMSAudits allProgramMessageAudits;
    @Autowired
    private AllBillAudits allBillAudits;

    @RequestMapping("/audits/sms")
    public void showAllSMSAudits(HttpServletResponse response) throws IOException {
        List<SMSAudit> messageAudits = allProgramMessageAudits.getAll();

        StringBuilder builder = new StringBuilder();
        builder.append("<div id='server_time'>" + DateUtil.now() + "</div>");
        builder.append("<table>");
        row(builder, header("Subscriber", "Program", "Sent on", "Content"));
        for (SMSAudit messageAudit : messageAudits) {
            List<? extends Object> dataList = asList(messageAudit.getSubscriberNumber(), messageAudit.getProgramName(), messageAudit.getSentTime().toString(), 
                    messageAudit.getContent());
            rowData(builder, dataList);
        }

        builder.append("</table>");
        response.getWriter().write(builder.toString());
    }

    @RequestMapping("/audits/bill")
    public void showAllBillAudits(HttpServletResponse response) throws IOException {
        List<BillAudit> billAudits = allBillAudits.getAll();

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
