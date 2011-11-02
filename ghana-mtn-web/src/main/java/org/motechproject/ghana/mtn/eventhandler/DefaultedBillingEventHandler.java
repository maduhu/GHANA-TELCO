package org.motechproject.ghana.mtn.eventhandler;

import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.process.BillingServiceMediator;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.*;

@Service
public class DefaultedBillingEventHandler {
    private AllSubscriptions allSubscriptions;
    private BillingServiceMediator billingServiceMediator;

    @Autowired
    public DefaultedBillingEventHandler(AllSubscriptions allSubscriptions, BillingServiceMediator billingServiceMediator) {
        this.allSubscriptions = allSubscriptions;
        this.billingServiceMediator = billingServiceMediator;
    }

    @MotechListener(subjects = {DEFAULTED_DAILY_SCHEDULE})
    public void checkDaily(MotechEvent event) {
        Map params = event.getParameters();
        String programKey = (String) params.get(PROGRAM_KEY);
        String subscriberNumber = (String) params.get(EXTERNAL_ID_KEY);

        Subscription defaultedSubscription = allSubscriptions.findBy(subscriberNumber, programKey, SubscriptionStatus.PAYMENT_DEFAULT);
        billingServiceMediator.chargeFeeForDefaultedSubscription(defaultedSubscription);
    }
}
