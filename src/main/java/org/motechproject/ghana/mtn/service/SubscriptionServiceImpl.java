package org.motechproject.ghana.mtn.service;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.validation.InputMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final static Logger log = Logger.getLogger(SubscriptionServiceImpl.class);
    private AllSubscribers allSubscribers;
    private AllSubscriptions allSubscriptions;

    @Autowired
    public SubscriptionServiceImpl(AllSubscribers allSubscribers, AllSubscriptions allSubscriptions) {
        this.allSubscribers = allSubscribers;
        this.allSubscriptions = allSubscriptions;
    }

    @Override
    public String enroll(SubscriptionRequest subscriptionRequest) {
        try {
            Subscription subscription = new InputMessageParser().parse(subscriptionRequest.getInputMessage());
            if (subscription.isValid()) {
                Subscriber subscriber = new Subscriber(subscriptionRequest.getSubscriberNumber());
                allSubscribers.add(subscriber);
                subscription.setSubscriber(subscriber);
                allSubscriptions.add(subscription);
                return String.format(MessageBundle.SUCCESSFUL_ENROLLMENT_MESSAGE_FORMAT, subscription.getType().getProgramName());
            }
        } catch (MessageParseFailException e) {
            log.error("Parsing failed.", e);
        }
        return MessageBundle.FAILURE_ENROLLMENT_MESSAGE;
    }

}
