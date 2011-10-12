package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.process.SubscriptionParser;
import org.motechproject.ghana.mtn.service.SMSHandler;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
    private SubscriptionParser parser;
    private SMSHandler smsHandler;

    @Autowired
    public SubscriptionController(SubscriptionParser parser, SMSHandler smsHandler) {
        this.parser = parser;
        this.smsHandler = smsHandler;
    }

    @RequestMapping("handle")
    public void handle(@ModelAttribute SubscriptionRequest request) {
        SMS sms = parser.process(request.getSubscriberNumber(), request.getInputMessage());

        if (sms == null) return;
        sms.process(smsHandler);
    }
}
