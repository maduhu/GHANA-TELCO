package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.process.UserMessageParserHandle;
import org.motechproject.ghana.mtn.service.SMSHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
    private UserMessageParserHandle parserHandle;
    private SMSHandler smsHandler;

    @Autowired
    public SubscriptionController(UserMessageParserHandle parserHandle, SMSHandler smsHandler) {
        this.parserHandle = parserHandle;
        this.smsHandler = smsHandler;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public void handle(@ModelAttribute SubscriptionRequest request) {
        SMS sms = parserHandle.process(request.getSubscriberNumber(), request.getInputMessage());

        if (sms == null) return;
        sms.process(smsHandler);
    }
}
