package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.billing.domain.MTNMockUser;
import org.motechproject.ghana.mtn.billing.repository.AllMTNMockUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class MockMTNController {

    @Autowired
    private AllMTNMockUsers allMTNMockUsers;

    @RequestMapping("/mock-mtn/users")
    public void showUsers(HttpServletResponse response) throws IOException {
        List<MTNMockUser> users = allMTNMockUsers.getAll();
        StringBuilder builder = new StringBuilder();
        builder.append("<table id=\"mtn_user_table\">");
        builder.append(header("Mobile No", "Balance"));
        for (MTNMockUser user : users)
            builder.append("<tr><td>" + user.getMobileNumber() + "</td><td>" + user.getBalance() + "</tr></td>");
        builder.append("</table>");
        response.getWriter().write(builder.toString());
    }

    private String header(Object... headers) {
        StringBuilder builder = new StringBuilder();
        for (Object header : headers)
            builder.append("<th>").append(header).append("</th>");
        return builder.toString();
    }

}
