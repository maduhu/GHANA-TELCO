package org.motechproject.ghana.mtn.process;

import org.hamcrest.Matchers;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

@Component
public class ValidationProcess extends BaseSubscriptionProcess implements ISubscriptionFlowProcess {
    private AllSubscriptions allSubscriptions;
    private BillingService billingService;

    @Autowired
    protected ValidationProcess(SMSService smsService, MessageBundle messageBundle,
                                AllSubscriptions allSubscriptions,
                                BillingService billingService) {
        super(smsService, messageBundle);
        this.allSubscriptions = allSubscriptions;
        this.billingService = billingService;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        String subscriberNumber = subscription.subscriberNumber();
        if (subscription.isNotValid()) {
            sendMessage(subscription, messageFor(MessageBundle.ENROLLMENT_FAILURE));
            return false;
        }
        if (hasActiveSubscription(subscriberNumber, subscription)) {
            String content = String.format(messageFor(MessageBundle.ACTIVE_SUBSCRIPTION_PRESENT), subscription.programName());
            sendMessage(subscription, content);
            return false;
        }
        BillingServiceRequest request = new BillingServiceRequest(subscriberNumber, subscription.getProgramType());
        BillingServiceResponse response = billingService.checkIfUserHasFunds(request);
        if (response.hasErrors()) {
            sendMessage(subscription, messageFor(response.getValidationErrors()));
            return false;
        }
        return true;
    }

    private boolean hasActiveSubscription(String subscriberNumber, Subscription subscription) {
        List<Subscription> activeSubscriptions = allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber);
        List<Subscription> subscriptions = select(activeSubscriptions, having(on(Subscription.class).getProgramType(),
                new ProgramTypeMatcher(subscription.getProgramType())));
        return !CollectionUtils.isEmpty(subscriptions);
    }

    @Override
    public Boolean stopExpired(Subscription subscription) {
        return true;
    }

    @Override
    public Boolean stopByUser(Subscription subscription) {
        return true;
    }

    @Override
    public Boolean rollOver(Subscription fromSubscription, Subscription toSubscription) {
        return fromSubscription.canRollOff();
    }

    public Subscription validateSubscriptionToStop(String subscriberNumber, IProgramType programType) {

        List<Subscription> subscriptions = allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber);
        boolean isUserWith2ProgrammesNotSpecifyProgramToStop = subscriptions.size() > 1 && programType == null;

        if(subscriptions.size() == 0)  {
            sendMessage(subscriberNumber, messageFor(MessageBundle.STOP_NOT_ENROLLED));
        } else if (isUserWith2ProgrammesNotSpecifyProgramToStop) {
            sendMessage(subscriberNumber, messageFor(MessageBundle.STOP_SPECIFY_PROGRAM));
        } else {
            Subscription subscriptionToStop = programType != null ?
                (Subscription) selectUnique(subscriptions, having(on(Subscription.class).programName(), equalTo(programType.getProgramName()))) :
                                    subscriptions.get(0);
            if(subscriptionToStop == null) sendMessage(subscriberNumber, messageFor(MessageBundle.STOP_NOT_ENROLLED));
            return subscriptionToStop;
        }
        return null;
    }
}
