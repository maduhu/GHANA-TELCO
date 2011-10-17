package org.motechproject.ghana.mtn.eventhandler;

import org.motechproject.ghana.mtn.billing.service.BillingScheduler;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.process.FeeChargerProcess;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.lang.String.format;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.EXTERNAL_ID_KEY;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.PROGRAM_KEY;

@Service
public class BillingEventHandler {
    private AllSubscriptions allSubscriptions;
    private FeeChargerProcess feeCharger;

    @Autowired
    public BillingEventHandler(AllSubscriptions allSubscriptions, FeeChargerProcess feeCharger) {
        this.allSubscriptions = allSubscriptions;
        this.feeCharger = feeCharger;
    }

    @MotechListener(subjects = {BillingScheduler.MONTHLY_BILLING_SCHEDULE_SUBJECT})
    public void chargeCustomer(MotechEvent event) {
        Map params = event.getParameters();
        String programName = (String) params.get(PROGRAM_KEY);
        String subscriberNumber = (String) params.get(EXTERNAL_ID_KEY);

        Subscription subscription = allSubscriptions.findBy(subscriberNumber, programName);
        feeCharger.process(subscription);
    }

}
