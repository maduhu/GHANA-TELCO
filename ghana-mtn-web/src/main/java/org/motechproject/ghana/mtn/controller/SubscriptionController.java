package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.domain.dto.SubscriptionServiceRequest;
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
    public static final String JSON_PREFIX = "{\"responseText\" : \"";
    public static final String JSON_SUFFIX = "\"}";
    public static final String CONTENT_TYPE_JSON = "application/json";

    private SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @RequestMapping("enroll")
    public void enroll(@ModelAttribute SubscriptionServiceRequest subscriptionRequest) {
        subscriptionService.startFor(subscriptionRequest);
    }
}
