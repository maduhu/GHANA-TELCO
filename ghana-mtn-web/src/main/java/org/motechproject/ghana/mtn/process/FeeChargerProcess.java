package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class FeeChargerProcess extends BaseSubscriptionProcess {

    private BillingService billingService;

    @Autowired
    public FeeChargerProcess(SMSService smsService, MessageBundle messageBundle, BillingService billingService) {
        super(smsService, messageBundle);
        this.billingService = billingService;
    }

    public void process(Subscription subscription) {
        String subscriberNumber = subscription.subscriberNumber();
        BillingServiceResponse<CustomerBill> response = billingService.chargeProgramFee(new BillingServiceRequest(subscriberNumber, subscription.getProgramType()));
        if (response.hasErrors())
            sendMessage(subscription, messageFor(response.getValidationErrors()));

        else
            sendMessage(subscription, format(messageFor(MessageBundle.BILLING_SUCCESS), response.getValue().amountChargedWithCurrency()));
    }
}
