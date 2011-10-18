package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.billing.domain.MTNMockUser;
import org.motechproject.ghana.mtn.billing.repository.AllMTNMockUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/mock-mtn")
public class MockMTNController {

    @Autowired
    private AllMTNMockUsers allMTNMockUsers;

    @RequestMapping("/users")
    public void showUsers() {
        List<MTNMockUser> users = allMTNMockUsers.getAll();
        StringBuilder builder = new StringBuilder();
        builder.append("<table>");
        builder.append(header("Mobile No", "Balance"));
        for (MTNMockUser user : users)
            builder.append("<tr><td>" + user.getMobileNumber() + "</td><td>" + user.getBalance() + "</tr></td>");
        builder.append("</table>");
    }

    private String header(Object... headers) {
        StringBuilder builder = new StringBuilder();
        for (Object header : headers)
            builder.append("<th>").append(header).append("</th>");
        return builder.toString();
    }

}
