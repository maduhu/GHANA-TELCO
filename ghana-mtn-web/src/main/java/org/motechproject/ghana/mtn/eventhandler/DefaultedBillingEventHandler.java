package org.motechproject.ghana.mtn.eventhandler;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.process.BillingServiceMediator;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        String programKey = (String) event.getParameters().get(PROGRAM_KEY);
        String subscriberNumber = (String) event.getParameters().get(EXTERNAL_ID_KEY);
        Subscription defaultedSubscription = defaultedSubscription(subscriberNumber, programKey);

        if(defaultedSubscription != null )
            billingServiceMediator.chargeFeeForDefaultedSubscriptionDaily(defaultedSubscription);
        else
            logWarning(programKey, subscriberNumber, DEFAULTED_DAILY_SCHEDULE);
    }

    @MotechListener(subjects = {DEFAULTED_WEEKLY_SCHEDULE})
    public void checkWeekly(MotechEvent event) {
        String programKey = (String) event.getParameters().get(PROGRAM_KEY);
        String subscriberNumber = (String) event.getParameters().get(EXTERNAL_ID_KEY);
        Subscription defaultedSubscription = defaultedSubscription(subscriberNumber, programKey);

        if(defaultedSubscription != null )
            billingServiceMediator.chargeFeeForDefaultedSubscriptionWeekly(defaultedSubscription);
        else
            logWarning(subscriberNumber, programKey, DEFAULTED_WEEKLY_SCHEDULE);
    }

    private Subscription defaultedSubscription(String subscriberNumber, String programKey) {
        return allSubscriptions.findBy(subscriberNumber, programKey, PAYMENT_DEFAULT);
    }

    private void logWarning(String programKey, String subscriberNumber, String subject) {
        log.warn("DefaultBillingSchedule-" + subject + " : SN-" + subscriberNumber + " |ProgramKey-" + programKey);
    }
}
