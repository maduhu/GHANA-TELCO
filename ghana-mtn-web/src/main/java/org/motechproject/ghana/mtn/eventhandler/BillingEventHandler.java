package org.motechproject.ghana.mtn.eventhandler;

import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.service.BillingScheduler;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.lang.String.format;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.EXTERNAL_ID_KEY;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.PROGRAM;

@Service
public class BillingEventHandler {
    private AllSubscriptions allSubscriptions;
    private BillingService billingService;
    private SMSService smsService;
    private MessageBundle messageBundle;

    @Autowired
    public BillingEventHandler(AllSubscriptions allSubscriptions, BillingService billingService, SMSService smsService, MessageBundle messageBundle) {
        this.allSubscriptions = allSubscriptions;
        this.billingService = billingService;
        this.smsService = smsService;
        this.messageBundle = messageBundle;
    }

    @MotechListener(subjects = {BillingScheduler.MONTHLY_BILLING_SCHEDULE_SUBJECT})
    public void chargeCustomer(MotechEvent motechEvent) {
        Map params = motechEvent.getParameters();
        String programName = (String) params.get(PROGRAM);
        String subscriberNumber = (String) params.get(EXTERNAL_ID_KEY);

        Subscription subscription = allSubscriptions.findBy(subscriberNumber, programName);
        BillingServiceResponse<CustomerBill> response = billingService.chargeProgramFee(new BillingServiceRequest(subscriberNumber, subscription.getProgramType()));
        if(!response.hasErrors())
            sms(subscriberNumber, subscription, format(messageBundle.get(MessageBundle.BILLING_SUCCESS),response.getValue().amountChargedWithCurrency()));
    }

    private void sms(String subscriberNumber, Subscription subscription, String message) {
        SMSServiceRequest request = new SMSServiceRequest(subscriberNumber, message, subscription.getProgramType());
        smsService.send(request);
    }

}
