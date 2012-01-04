package org.motechproject.ghana.telco.process;

import org.motechproject.ghana.telco.domain.*;
import org.motechproject.ghana.telco.exception.InvalidProgramException;
import org.motechproject.ghana.telco.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.telco.repository.AllShortCodes;
import org.motechproject.ghana.telco.repository.AllSubscriptions;
import org.motechproject.ghana.telco.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.motechproject.ghana.telco.domain.MessageBundle.*;
import static org.motechproject.ghana.telco.domain.ShortCode.RETAIN_EXISTING_CHILDCARE_PROGRAM;
import static org.motechproject.ghana.telco.domain.ShortCode.USE_ROLLOVER_TO_CHILDCARE_PROGRAM;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class ValidationProcess extends BaseSubscriptionProcess implements ISubscriptionFlowProcess {
    private AllSubscriptions allSubscriptions;
    private AllShortCodes allShortCodes;

    @Autowired
    protected ValidationProcess(SMSService smsService, MessageBundle messageBundle,
                                AllSubscriptions allSubscriptions,
                                AllShortCodes allShortCodes) {
        super(smsService, messageBundle);
        this.allSubscriptions = allSubscriptions;
        this.allShortCodes = allShortCodes;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        String subscriberNumber = subscription.subscriberNumber();
        if (subscription.isNotValid()) {
            sendMessage(subscription, messageFor(REQUEST_FAILURE));
            return false;
        }
        if (hasActiveSubscription(subscriberNumber, subscription)) {
            String content = format(messageFor(MessageBundle.ACTIVE_SUBSCRIPTION_PRESENT), subscription.programName());
            sendMessage(subscription, content);
            return false;
        }        
        return true;
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
        Subscription existingChildCareSubscription = allSubscriptions.findActiveSubscriptionFor(fromSubscription.subscriberNumber(), ProgramType.CHILDCARE);
        if (existingChildCareSubscription != null) {
            String retainExistingCCProgramShortCode = formatShortCode(allShortCodes.getShortCodeFor(RETAIN_EXISTING_CHILDCARE_PROGRAM));
            String rollOverToNewCCProgramShortCode = formatShortCode(allShortCodes.getShortCodeFor(USE_ROLLOVER_TO_CHILDCARE_PROGRAM));

            sendMessage(existingChildCareSubscription.subscriberNumber(), format(messageFor(ROLLOVER_NOT_POSSIBLE_PROGRAM_EXISTS_ALREADY),
                    retainExistingCCProgramShortCode, rollOverToNewCCProgramShortCode));

            fromSubscription.setStatus(SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE);
            allSubscriptions.update(fromSubscription);
            return true;
        }
        return fromSubscription.canRollOff();
    }

    @Override
    public Boolean retainExistingChildCare(Subscription pregnancySubscriptionWaitingForRollOver, Subscription childCareSubscription) {
        if (pregnancySubscriptionWaitingForRollOver == null)
            throw new InvalidProgramException(messageFor(ROLLOVER_NO_PENDING_PREGNANCY_PROGRAM));
        if (childCareSubscription == null) {
            sendMessage(pregnancySubscriptionWaitingForRollOver.subscriberNumber(), messageFor(ROLLOVER_NO_PENDING_PREGNANCY_PROGRAM));
            return false;
        }
        return true;
    }

    @Override
    public Boolean rollOverToNewChildCareProgram(Subscription pregnancyProgramWaitingForRollOver, Subscription newChildCareToRollOver, Subscription existingChildCare) {
        return retainExistingChildCare(pregnancyProgramWaitingForRollOver, existingChildCare);
    }

    public Subscription validateSubscriptionToStop(String subscriberNumber, ProgramType programType) {

        List<Subscription> subscriptions = allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber);
        boolean isUserWith2ProgrammesDidNotSpecifyProgramToStop = subscriptions.size() > 1 && programType == null;

        if (subscriptions.size() == 0) {
            sendMessage(subscriberNumber, messageFor(NOT_ENROLLED));
        } else if (isUserWith2ProgrammesDidNotSpecifyProgramToStop) {
            sendMessage(subscriberNumber, messageFor(MessageBundle.STOP_SPECIFY_PROGRAM));
        } else {
            Subscription subscriptionToStop = programType != null ?
                    (Subscription) selectUnique(subscriptions, having(on(Subscription.class).programKey(), equalTo(programType.getProgramKey()))) :
                    subscriptions.get(0);
            if (subscriptionToStop == null) sendMessage(subscriberNumber, messageFor(NOT_ENROLLED));
            return subscriptionToStop;
        }
        return null;
    }

    public Subscription validateForRollOver(String subscriberNumber, Date deliveryDate) {
        Subscription subscription = allSubscriptions.findActiveSubscriptionFor(subscriberNumber, ProgramType.PREGNANCY);
        if (null == subscription)
            sendMessage(subscriberNumber, messageFor(MessageBundle.ROLLOVER_INVALID_SUBSCRIPTION));
        return subscription;
    }

    private boolean hasActiveSubscription(String subscriberNumber, Subscription subscription) {
        List<Subscription> activeSubscriptions = allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber);
        List<Subscription> subscriptions = select(activeSubscriptions, having(on(Subscription.class).getProgramType(),
                new ProgramTypeMatcher(subscription.getProgramType())));
        return !isEmpty(subscriptions);
    }

    private String formatShortCode(ShortCode shortCode) {
        return isEmpty(shortCode.getCodes()) ? "" : shortCode.defaultCode();
    }
}
