package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.domain.ProgramMessageAudit;
import org.motechproject.ghana.mtn.repository.AllProgramMessageAudits;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class ProgramMessageAuditController {
    @Autowired
    private AllProgramMessageAudits allProgramMessageAudits;

    @RequestMapping("/audits")
    public void showAll(HttpServletResponse response) throws IOException {
        List<ProgramMessageAudit> messageAudits = allProgramMessageAudits.getAll();

        StringBuilder builder = new StringBuilder();
        builder.append("<div id='server_time'>" + DateUtil.now() + "</div>");
        builder.append("<table>");
        builder.append("<tr><th>Subscriber</th><th>Program</th><th>Sent On</th><th>Content</th></tr>");
        for (ProgramMessageAudit messageAudit : messageAudits)
            builder.append(
                    "<tr><td>" + messageAudit.getSubscriberNumber()
                            + "</td><td>" + messageAudit.getProgramName()
                            + "</td><td>" + messageAudit.getSentTime()
                            + "</td><td>" + messageAudit.getContent()
                            + "</td></tr>");

        builder.append("</table>");
        response.getWriter().write(builder.toString());
    }
}
