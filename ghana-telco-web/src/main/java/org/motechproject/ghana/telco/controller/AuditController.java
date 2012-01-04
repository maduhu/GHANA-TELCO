package org.motechproject.ghana.telco.controller;

import org.joda.time.DateTime;
import org.motechproject.ghana.telco.domain.SMSAudit;
import org.motechproject.ghana.telco.repository.AllSMSAudits;
import org.motechproject.ghana.telco.repository.AllSubscriptions;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    private Comparator<DateTime> sortComparator() {
        return new Comparator<DateTime>() {
            @Override
            public int compare(DateTime o1, DateTime o2) {
                return o2.compareTo(o1);
            }
        };
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
