package org.motechproject.ghana.telco.controller;

import org.motechproject.ghana.telco.domain.SMS;
import org.motechproject.ghana.telco.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.telco.process.UserMessageParserProcess;
import org.motechproject.ghana.telco.service.SMSHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
    private UserMessageParserProcess subscriptionParser;
    private SMSHandler smsHandler;

    @Autowired
    public SubscriptionController(UserMessageParserProcess subscriptionParser, SMSHandler smsHandler) {
        this.subscriptionParser = subscriptionParser;
        this.smsHandler = smsHandler;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public void handle(@ModelAttribute SubscriptionRequest request) {
        SMS sms = subscriptionParser.process(request.getSubscriberNumber(), request.getInputMessage());
        if (sms == null) return;
        sms.process(smsHandler);
    }
}
