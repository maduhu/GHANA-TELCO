package org.motechproject.ghana.mtn.service;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.motechproject.ghana.mtn.matchers.SubscriptionTypeMatcher;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.validation.InputMessageParser;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static ch.lambdaj.Lambda.*;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final static Logger log = Logger.getLogger(SubscriptionServiceImpl.class);
    private AllSubscribers allSubscribers;
    private AllSubscriptions allSubscriptions;
    private MessageCampaignService campaignService;
    private InputMessageParser inputMessageParser;

    @Autowired
    public SubscriptionServiceImpl(AllSubscribers allSubscribers, AllSubscriptions allSubscriptions,
                                   MessageCampaignService campaignService, InputMessageParser inputMessageParser) {
        this.allSubscribers = allSubscribers;
        this.allSubscriptions = allSubscriptions;
        this.campaignService = campaignService;
        this.inputMessageParser = inputMessageParser;
    }

    @Override
    public String enroll(SubscriptionRequest subscriptionRequest) {
        try {
            String subscriberNumber = subscriptionRequest.getSubscriberNumber();
            Subscription subscription = inputMessageParser.parse(subscriptionRequest.getInputMessage());

            if (subscription.isNotValid())
                return MessageBundle.FAILURE_ENROLLMENT_MESSAGE;
            if (hasActiveSubscription(subscriberNumber, subscription))
                return format(MessageBundle.ACTIVE_SUBSCRIPTION_ALREADY_PRESENT, subscription);

            persist(subscriberNumber, subscription);
            createCampaign(subscription);
            return format(MessageBundle.SUCCESSFUL_ENROLLMENT_MESSAGE_FORMAT, subscription);

        } catch (MessageParseFailException e) {
            log.error("Parsing failed.", e);
        }
        return MessageBundle.FAILURE_ENROLLMENT_MESSAGE;
    }

    @Override
    public Subscription findBy(String subscriberNumber, String programName) {
        return allSubscriptions.findBy(subscriberNumber, programName);
    }

    private void createCampaign(Subscription subscription) {
        CampaignRequest campaignRequest = subscription.createCampaignRequest();
        campaignService.startFor(campaignRequest);
    }

    private void persist(String subscriberNumber, Subscription subscription) {
        Subscriber subscriber = new Subscriber(subscriberNumber);
        allSubscribers.add(subscriber);
        subscription.setSubscriber(subscriber);
        allSubscriptions.add(subscription);
    }

    private String format(String message, Subscription subscription) {
        return String.format(message, subscription.programName());
    }

    private boolean hasActiveSubscription(String subscriberNumber, Subscription subscription) {
        List<Subscription> activeSubscriptions = allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber);
        List<Subscription> subscriptions = select(activeSubscriptions, having(on(Subscription.class).getSubscriptionType(),
                new SubscriptionTypeMatcher(subscription.getSubscriptionType())));
        return !CollectionUtils.isEmpty(subscriptions);
    }
}
