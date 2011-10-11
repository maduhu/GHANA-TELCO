package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionServiceRequest;
import org.motechproject.ghana.mtn.service.process.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private SubscriptionValidation validation;
    private SubscriptionBilling billing;
    private SubscriptionPersistence persistence;
    private SubscriptionCampaign campaign;
    private SubscriptionParser parser;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionParser parser,
                                   SubscriptionValidation validation,
                                   SubscriptionBilling billing,
                                   SubscriptionPersistence persistence,
                                   SubscriptionCampaign campaign) {
        this.validation = validation;
        this.billing = billing;
        this.persistence = persistence;
        this.campaign = campaign;
        this.parser = parser;
    }

    @Override
    public void startFor(SubscriptionServiceRequest subscriptionRequest) {
        String subscriberNumber = subscriptionRequest.getSubscriberNumber();
        String inputMessage = subscriptionRequest.getInputMessage();
        Subscription subscription = parser.parseForEnrollment(subscriberNumber, inputMessage);
        if (subscription == null) return;

        Subscriber subscriber = new Subscriber(subscriberNumber);
        subscription.setSubscriber(subscriber);

        List<BaseSubscriptionProcess> processes = Arrays.asList(validation, billing, persistence, campaign);
        for (BaseSubscriptionProcess process : processes) {
            if (process.startFor(subscription)) continue;
            break;
        }
    }

    @Override
    public void endFor(SubscriptionServiceRequest subscriptionRequest) {
        String subscriberNumber = subscriptionRequest.getSubscriberNumber();
        String inputMessage = subscriptionRequest.getInputMessage();
        Subscription subscription = parser.parseForWithDraw(subscriberNumber, inputMessage);
        if (subscription == null) return;

        List<BaseSubscriptionProcess> processes = Arrays.asList(billing, persistence, campaign);
        for (BaseSubscriptionProcess process : processes) {
            if (process.endFor(subscription)) continue;
            break;
        }
    }

}
