package org.motechproject.ghana.mtn.service;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.RegistrationBillingRequest;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.motechproject.ghana.mtn.exception.UserRegistrationFailureException;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.validation.InputMessageParser;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.motechproject.ghana.mtn.domain.MessageBundle.*;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final static Logger log = Logger.getLogger(SubscriptionServiceImpl.class);
    private AllSubscribers allSubscribers;
    private AllSubscriptions allSubscriptions;
    private MessageCampaignService campaignService;
    private InputMessageParser inputMessageParser;
    private BillingService billingService;

    @Autowired
    public SubscriptionServiceImpl(AllSubscribers allSubscribers, AllSubscriptions allSubscriptions,
                                   MessageCampaignService campaignService, InputMessageParser inputMessageParser, BillingService billingService) {
        this.allSubscribers = allSubscribers;
        this.allSubscriptions = allSubscriptions;
        this.campaignService = campaignService;
        this.inputMessageParser = inputMessageParser;
        this.billingService = billingService;
    }

    @Override
    public String enroll(SubscriptionRequest subscriptionRequest) {
        try {
            String subscriberNumber = subscriptionRequest.getSubscriberNumber();
            Subscription subscription = inputMessageParser.parse(subscriptionRequest.getInputMessage());
            
            validateMessageAndSubscriber(subscriberNumber, subscription);
            processBillingAndCreateSchedule(subscriberNumber, subscription);
            persist(subscriberNumber, subscription);
            createCampaign(subscription);
            return format(getMessage(SUCCESSFUL_ENROLLMENT_MESSAGE_FORMAT), subscription);

        } catch (MessageParseFailException e) {
            log.error("Parsing failed.", e);
        } catch (UserRegistrationFailureException e) {
            log.error("User registration failed.", e);
            if(isNotEmpty(e.getMessage())) return e.getMessage();
        }
        return getMessage(FAILURE_ENROLLMENT_MESSAGE);
    }

    private void validateMessageAndSubscriber(String subscriberNumber, Subscription subscription) {
        if (subscription.isNotValid())
            throw new UserRegistrationFailureException(getMessage(FAILURE_ENROLLMENT_MESSAGE));
        if (hasActiveSubscription(subscriberNumber, subscription))
            throw new UserRegistrationFailureException(format(getMessage(ACTIVE_SUBSCRIPTION_ALREADY_PRESENT), subscription));

        BillingServiceResponse serviceResponse = billingService.hasFundsForProgram(new BillingServiceRequest(subscriberNumber, subscription.getProgramType()));
        if (serviceResponse.hasErrors())
            throw new UserRegistrationFailureException(getUserSMSResponseMessage(serviceResponse));
    }

    @Override
    public Subscription findBy(String subscriberNumber, String programName) {
        return allSubscriptions.findBy(subscriberNumber, programName);
    }

    private String getUserSMSResponseMessage(BillingServiceResponse serviceResponse) {
        String message = getMessage(((ValidationError) serviceResponse.getValidationErrors().get(0)).name());
        return message != null ? message : getMessage(FAILURE_ENROLLMENT_MESSAGE);
    }

    private void processBillingAndCreateSchedule(String subscriberNumber, Subscription subscription) {
        RegistrationBillingRequest registrationBillingRequest = new RegistrationBillingRequest(subscriberNumber, subscription.getProgramType(), subscription.cycleStartDate());
        BillingServiceResponse response = billingService.processRegistration(registrationBillingRequest);
        if(response.hasErrors()) throw new UserRegistrationFailureException(getUserSMSResponseMessage(response));

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.updateStartCycleInfo();
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
        List<Subscription> subscriptions = select(activeSubscriptions, having(on(Subscription.class).getProgramType(),
                new ProgramTypeMatcher(subscription.getProgramType())));
        return !CollectionUtils.isEmpty(subscriptions);
    }
}
