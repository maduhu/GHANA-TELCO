package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.service.process.SubscriptionParser;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
    private SubscriptionService service;
    private SubscriptionParser parser;

    @Autowired
    public SubscriptionController(SubscriptionService service, SubscriptionParser parser) {
        this.service = service;
        this.parser = parser;
    }

    @RequestMapping("handle")
    public void handle(@ModelAttribute SubscriptionRequest request) {
        Subscription subscription = parser.parse(request.getSubscriberNumber(), request.getInputMessage());
        if (subscription == null) return;

        Subscriber subscriber = new Subscriber(request.getSubscriberNumber());
        subscription.setSubscriber(subscriber);
        service.start(subscription);
    }
}
