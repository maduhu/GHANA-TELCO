package org.motechproject.ghana.mtn.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.RegistrationBillingRequest;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
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

//TODO needs refactoring, has many responsibilities
@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final static Logger log = Logger.getLogger(SubscriptionServiceImpl.class);
    private AllSubscribers allSubscribers;
    private AllSubscriptions allSubscriptions;
    private MessageCampaignService campaignService;
    private InputMessageParser inputMessageParser;
    private BillingService billingService;
    private MessageBundle messageBundle;

    @Autowired
    public SubscriptionServiceImpl(AllSubscribers allSubscribers, AllSubscriptions allSubscriptions,
                                   MessageCampaignService campaignService, InputMessageParser inputMessageParser,
                                   BillingService billingService, MessageBundle messageBundle) {
        this.allSubscribers = allSubscribers;
        this.allSubscriptions = allSubscriptions;
        this.campaignService = campaignService;
        this.inputMessageParser = inputMessageParser;
        this.billingService = billingService;
        this.messageBundle = messageBundle;
    }

    @Override
    public String enroll(SubscriptionRequest subscriptionRequest) {
        try {
            String subscriberNumber = subscriptionRequest.getSubscriberNumber();
            Subscription subscription = inputMessageParser.parse(subscriptionRequest.getInputMessage());

            validateSubscriber(subscriberNumber, subscription);
            billingAndStartMonthlySchedule(subscriberNumber, subscription);
            persist(subscriberNumber, subscription);
            createCampaign(subscription);
            return format(messageBundle.get(ENROLLMENT_SUCCESS), subscription);

        } catch (MessageParseFailException e) {
            log.error("Parsing failed.", e);
        } catch (UserRegistrationFailureException e) {
            log.error("User registration failed.", e);
            if (isNotEmpty(e.getMessage())) return e.getMessage();
        }
        return messageBundle.get(ENROLLMENT_FAILURE);
    }

    private void validateSubscriber(String subscriberNumber, Subscription subscription) {
        if (subscription.isNotValid())
            throw new UserRegistrationFailureException(messageBundle.get(ENROLLMENT_FAILURE));
        if (hasActiveSubscription(subscriberNumber, subscription))
            throw new UserRegistrationFailureException(format(messageBundle.get(ACTIVE_SUBSCRIPTION_PRESENT), subscription));

        BillingServiceRequest request = new BillingServiceRequest(subscriberNumber, subscription.getProgramType());
        BillingServiceResponse response = billingService.checkIfUserHasFunds(request);
        if (response.hasErrors())
            throw new UserRegistrationFailureException(getUserSMSResponse(response));
    }

    private void billingAndStartMonthlySchedule(String subscriberNumber, Subscription subscription) {
        RegistrationBillingRequest registrationBillingRequest = new RegistrationBillingRequest(subscriberNumber, subscription.getProgramType(), subscription.billingStartDate());
        BillingServiceResponse response = billingService.processRegistration(registrationBillingRequest);
        if (response.hasErrors())
            throw new UserRegistrationFailureException(getUserSMSResponse(response));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.updateStartCycleInfo();
    }

    @Override
    public Subscription findBy(String subscriberNumber, String programName) {
        return allSubscriptions.findBy(subscriberNumber, programName);
    }

    private String getUserSMSResponse(BillingServiceResponse serviceResponse) {
        List<ValidationError> validationErrors = serviceResponse.getValidationErrors();
        StringBuilder builder = new StringBuilder();
        for (ValidationError validationError : validationErrors) {
            builder.append(messageBundle.get(validationError) + " ");
        }
        String message = builder.toString();
        return message != null ? StringUtils.trim(message) : messageBundle.get(ENROLLMENT_FAILURE);
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
