package org.motechproject.ghana.mtn.service.process;

import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.service.sms.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionBilling extends BaseSubscriptionProcess implements ISubscriptionProcessFlow {
    private BillingService billingService;

    @Autowired
    public SubscriptionBilling(BillingService billingService, SMSService smsService, MessageBundle messageBundle) {
        super(smsService, messageBundle);
        this.billingService = billingService;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        BillingCycleRequest request = new BillingCycleRequest(subscription.subscriberNumber(), subscription.getProgramType(), subscription.billingStartDate());
        BillingServiceResponse<CustomerBill> response = billingService.startBilling(request);
        if (response.hasErrors()) {
            sendMessage(subscription, messageFor(response.getValidationErrors()));
            return false;
        }
        String content = String.format(messageFor(MessageBundle.BILLING_SUCCESS), response.getValue().amountCharged());
        sendMessage(subscription, content);
        return true;
    }

    @Override
    public Boolean stopFor(Subscription subscription) {
        BillingCycleRequest request = new BillingCycleRequest(subscription.subscriberNumber(), subscription.getProgramType(), subscription.billingStartDate());
        BillingServiceResponse<CustomerBill> response = billingService.stopBilling(request);
        if (response.hasErrors()) {
            sendMessage(subscription, messageFor(response.getValidationErrors()));
            return false;
        }
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        sendMessage(subscription, messageFor(MessageBundle.BILLING_STOPPED));
        return true;
    }
}
