package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

@Component
public class PersistenceProcess extends BaseSubscriptionProcess implements ISubscriptionFlowProcess {
    private AllSubscribers allSubscribers;
    private AllSubscriptions allSubscriptions;

    @Autowired
    public PersistenceProcess(AllSubscribers allSubscribers, AllSubscriptions allSubscriptions, SMSService smsService, MessageBundle messageBundle) {
        super(smsService, messageBundle);
        this.allSubscribers = allSubscribers;
        this.allSubscriptions = allSubscriptions;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.updateStartCycleInfo();
        allSubscribers.add(subscription.getSubscriber());
        allSubscriptions.add(subscription);
        return true;
    }

    @Override
    public Boolean stopExpired(Subscription subscription) {
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        allSubscriptions.update(subscription);
        return true;
    }

    @Override
    public Boolean stopByUser(Subscription subscription) {
        subscription.setStatus(SubscriptionStatus.SUSPENDED);
        allSubscriptions.update(subscription);
        return true;
    }

    @Override
    public Boolean rollOver(Subscription fromSubscription, Subscription toSubscription) {
        if (!WAITING_FOR_ROLLOVER_RESPONSE.equals(fromSubscription.getStatus())) {
            fromSubscription.setStatus(SubscriptionStatus.ROLLED_OFF);
            toSubscription.setStatus(fromSubscription.isPaymentDefaulted() ? SubscriptionStatus.PAYMENT_DEFAULT : SubscriptionStatus.ACTIVE);
            toSubscription.updateStartCycleInfo();
            allSubscriptions.add(toSubscription);
        }
        allSubscriptions.update(fromSubscription);
        return true;
    }

    @Override
    public Boolean retainExistingChildCare(Subscription subscription) {
        return true;
    }


}
