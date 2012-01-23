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

@Service
public class BillingEventHandler {
    private AllSubscriptions allSubscriptions;
    private BillingServiceMediator billingServiceMediator;
    private final Logger log = Logger.getLogger(BillingEventHandler.class);

    @Autowired
    public BillingEventHandler(AllSubscriptions allSubscriptions, BillingServiceMediator billingServiceMediator) {
        this.allSubscriptions = allSubscriptions;
        this.billingServiceMediator = billingServiceMediator;
    }

    @MotechListener(subjects = {MONTHLY_BILLING_SCHEDULE_SUBJECT})
    public void chargeCustomer(MotechEvent event) {
        Map params = event.getParameters();
        String programKey = (String) params.get(PROGRAM_KEY);
        String subscriberNumber = (String) params.get(EXTERNAL_ID_KEY);

        Subscription subscription = allSubscriptions.findActiveSubscriptionFor(subscriberNumber, programKey);
        billingServiceMediator.chargeMonthlyFeeAndHandleIfDefaulted(subscription);
        log.info("Billing Scheduler - monthy for subscriber:" + subscriberNumber  +" |Program:" + programKey);
    }
}
