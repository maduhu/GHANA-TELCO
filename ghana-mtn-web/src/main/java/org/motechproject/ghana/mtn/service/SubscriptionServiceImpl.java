package org.motechproject.ghana.mtn.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
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
    private SMSService smsService;

    @Autowired
    public SubscriptionServiceImpl(AllSubscribers allSubscribers, AllSubscriptions allSubscriptions,
                                   MessageCampaignService campaignService, InputMessageParser inputMessageParser,
                                   BillingService billingService, MessageBundle messageBundle, SMSService smsService) {
        this.allSubscribers = allSubscribers;
        this.allSubscriptions = allSubscriptions;
        this.campaignService = campaignService;
        this.inputMessageParser = inputMessageParser;
        this.billingService = billingService;
        this.messageBundle = messageBundle;
        this.smsService = smsService;
    }

    @Override
    public Subscription findBy(String subscriberNumber, String programName) {
        return allSubscriptions.findBy(subscriberNumber, programName);
    }

    @Override
    public String enroll(SubscriptionRequest subscriptionRequest) {

        String messageToSend;
        String subscriberNumber = subscriptionRequest.getSubscriberNumber();
        Subscription subscription = null;
        try {
            subscription = inputMessageParser.parse(subscriptionRequest.getInputMessage());
            validateSubscriber(subscriberNumber, subscription);
            billingAndStartMonthlySchedule(subscriberNumber, subscription);
            persist(subscriberNumber, subscription);
            createCampaign(subscription);
            messageToSend = format(message(ENROLLMENT_SUCCESS), subscription);

        } catch (MessageParseFailException e) {
            log.error("Parsing failed.", e);
            messageToSend = message(ENROLLMENT_FAILURE);
        } catch (UserRegistrationFailureException e) {
            log.error("User registration failed.", e);
            messageToSend = isNotEmpty(e.getMessage()) ? e.getMessage() : message(ENROLLMENT_FAILURE);
        }
        return sendSms(subscriberNumber, subscription != null ? subscription.getProgramType() : null, messageToSend);
    }

    private void validateSubscriber(String subscriberNumber, Subscription subscription) {
        if (subscription.isNotValid())
            throw new UserRegistrationFailureException(message(ENROLLMENT_FAILURE));
        if (hasActiveSubscription(subscriberNumber, subscription))
            throw new UserRegistrationFailureException(format(message(ACTIVE_SUBSCRIPTION_PRESENT), subscription));

        BillingServiceRequest request = new BillingServiceRequest(subscriberNumber, subscription.getProgramType());
        BillingServiceResponse response = billingService.checkIfUserHasFunds(request);
        if (response.hasErrors())
            throw new UserRegistrationFailureException(getUserSMSResponse(response));
    }

    private void billingAndStartMonthlySchedule(String subscriberNumber, Subscription subscription) {
        BillingCycleRequest billingCycleRequest = new BillingCycleRequest(subscriberNumber, subscription.getProgramType(), subscription.billingStartDate());
        BillingServiceResponse<CustomerBill> response = billingService.startBillingCycle(billingCycleRequest);
        if (response.hasErrors())
            throw new UserRegistrationFailureException(getUserSMSResponse(response));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.updateStartCycleInfo();
        sendSms(subscriberNumber, subscription.getProgramType(), String.format(message(BILLING_SUCCESS), response.getValue().getAmountCharged()));
    }

    private String sendSms(String subscriberNumber, IProgramType programType, String message) {
        smsService.send(new SMSServiceRequest(subscriberNumber, message, programType));
        return message;
    }

    private String getUserSMSResponse(BillingServiceResponse serviceResponse) {
        List<ValidationError> validationErrors = serviceResponse.getValidationErrors();
        StringBuilder builder = new StringBuilder();
        for (ValidationError validationError : validationErrors) {
            builder.append(messageBundle.get(validationError) + " ");
        }
        String message = builder.toString();
        return message != null ? StringUtils.trim(message) : message(ENROLLMENT_FAILURE);
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

    private String message(String key) {
        return messageBundle.get(key);
    }
}
