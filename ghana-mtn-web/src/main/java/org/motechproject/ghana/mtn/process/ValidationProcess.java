package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.ShortCode;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.repository.AllShortCodes;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.motechproject.ghana.mtn.domain.MessageBundle.ROLLOVER_NOT_POSSIBLE_PROGRAM_EXISTS_ALREADY;
import static org.motechproject.ghana.mtn.domain.ShortCode.RETAIN_EXISTING_CHILDCARE_PROGRAM;
import static org.motechproject.ghana.mtn.domain.ShortCode.USE_ROLLOVER_TO_CHILDCARE_PROGRAM;

@Component
public class ValidationProcess extends BaseSubscriptionProcess implements ISubscriptionFlowProcess {
    private AllSubscriptions allSubscriptions;
    private BillingService billingService;
    private AllShortCodes allShortCodes;

    @Autowired
    protected ValidationProcess(SMSService smsService, MessageBundle messageBundle,
                                AllSubscriptions allSubscriptions,
                                BillingService billingService, AllShortCodes allShortCodes) {
        super(smsService, messageBundle);
        this.allSubscriptions = allSubscriptions;
        this.billingService = billingService;
        this.allShortCodes = allShortCodes;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        String subscriberNumber = subscription.subscriberNumber();
        if (subscription.isNotValid()) {
            sendMessage(subscription, messageFor(MessageBundle.ENROLLMENT_FAILURE));
            return false;
        }
        if (hasActiveSubscription(subscriberNumber, subscription)) {
            String content = format(messageFor(MessageBundle.ACTIVE_SUBSCRIPTION_PRESENT), subscription.programName());
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
        Subscription subscription = allSubscriptions.findBy(fromSubscription.subscriberNumber(), IProgramType.CHILDCARE);
        if(subscription != null) {
            String retainExistingCCProgramShortCode = formatShortCode(allShortCodes.getAllCodesFor(RETAIN_EXISTING_CHILDCARE_PROGRAM));
            String rollOverToNewCCProgramShortCode = formatShortCode(allShortCodes.getAllCodesFor(USE_ROLLOVER_TO_CHILDCARE_PROGRAM));
            sendMessage(subscription.subscriberNumber(), format(messageFor(ROLLOVER_NOT_POSSIBLE_PROGRAM_EXISTS_ALREADY),
                    retainExistingCCProgramShortCode, rollOverToNewCCProgramShortCode));
           return false;
        }
        return fromSubscription.canRollOff();
    }

    private String formatShortCode(List<ShortCode> shortCodes) {
        return shortCodes.get(0)  != null ? shortCodes.get(0).defaultCode() : "";
    }

    public Subscription validateSubscriptionToStop(String subscriberNumber, IProgramType programType) {

        List<Subscription> subscriptions = allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber);
        boolean isUserWith2ProgrammesDidNotSpecifyProgramToStop = subscriptions.size() > 1 && programType == null;

        if(subscriptions.size() == 0)  {
            sendMessage(subscriberNumber, messageFor(MessageBundle.STOP_NOT_ENROLLED));
        } else if (isUserWith2ProgrammesDidNotSpecifyProgramToStop) {
            sendMessage(subscriberNumber, messageFor(MessageBundle.STOP_SPECIFY_PROGRAM));
        } else {
            Subscription subscriptionToStop = programType != null ?
                (Subscription) selectUnique(subscriptions, having(on(Subscription.class).programKey(), equalTo(programType.getProgramKey()))) :
                                    subscriptions.get(0);
            if(subscriptionToStop == null) sendMessage(subscriberNumber, messageFor(MessageBundle.STOP_NOT_ENROLLED));
            return subscriptionToStop;
        }
        return null;
    }

    public Subscription validateForRollOver(String subscriberNumber, Date deliveryDate) {
        Subscription subscription = allSubscriptions.findBy(subscriberNumber, IProgramType.PREGNANCY);
        if (null == subscription)
            sendMessage(subscriberNumber, messageFor(MessageBundle.ROLLOVER_INVALID_SUBSCRIPTION));
        return subscription;
    }
}
