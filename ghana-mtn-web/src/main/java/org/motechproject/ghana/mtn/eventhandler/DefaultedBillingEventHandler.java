package org.motechproject.ghana.mtn.eventhandler;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.process.BillingServiceMediator;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.*;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.PAYMENT_DEFAULT;

@Service
public class DefaultedBillingEventHandler {
    private AllSubscriptions allSubscriptions;
    private BillingServiceMediator billingServiceMediator;

    private final Logger log = Logger.getLogger(DefaultedBillingEventHandler.class);

    @Autowired
    public DefaultedBillingEventHandler(AllSubscriptions allSubscriptions, BillingServiceMediator billingServiceMediator) {
        this.allSubscriptions = allSubscriptions;
        this.billingServiceMediator = billingServiceMediator;
    }

    @MotechListener(subjects = {DEFAULTED_DAILY_SCHEDULE})
    public void checkDaily(MotechEvent event) {
        chargeFeeForDefaultedSubscription(event, DEFAULTED_DAILY_SCHEDULE);
    }

    @MotechListener(subjects = {DEFAULTED_WEEKLY_SCHEDULE})
    public void checkWeekly(MotechEvent event) {
        chargeFeeForDefaultedSubscription(event, DEFAULTED_WEEKLY_SCHEDULE);
    }

    private void chargeFeeForDefaultedSubscription(MotechEvent event, String subject) {
        Map params = event.getParameters();
        String programKey = (String) params.get(PROGRAM_KEY);
        String subscriberNumber = (String) params.get(EXTERNAL_ID_KEY);

        Subscription defaultedSubscription = allSubscriptions.findBy(subscriberNumber, programKey, PAYMENT_DEFAULT);
        if(defaultedSubscription != null )
            billingServiceMediator.chargeFeeForDefaultedSubscription(defaultedSubscription);
        else
            log.warn("DefaultBillingSchedule-" + subject + " : SN-" + subscriberNumber + " |ProgramKey-" + programKey);
    }
}
