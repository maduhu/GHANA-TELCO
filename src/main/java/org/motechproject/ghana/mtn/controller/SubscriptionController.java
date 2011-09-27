package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
    private SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @RequestMapping("/enroll")
    public void enroll(@ModelAttribute SubscriptionRequest subscriptionRequest, HttpServletResponse response) throws IOException {
        String status = subscriptionService.enroll(subscriptionRequest);
        response.setContentType("application/json");
        response.getWriter().write("{\"responseText\" : \"" + status + "\"}");
    }
}
