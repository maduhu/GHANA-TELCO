package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillingCycleProcess extends BaseSubscriptionProcess implements ISubscriptionFlowProcess {
    private BillingService billingService;

    @Autowired
    public BillingCycleProcess(BillingService billingService, SMSService smsService, MessageBundle messageBundle) {
        super(smsService, messageBundle);
        this.billingService = billingService;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        BillingCycleRequest request = new BillingCycleRequest(
                subscription.subscriberNumber(),
                subscription.getProgramType(),
                subscription.billingStartDate());
        return startFor(subscription, request);
    }

    @Override
    public Boolean stopExpired(Subscription subscription) {
        BillingCycleRequest request = new BillingCycleRequest(
                subscription.subscriberNumber(),
                subscription.getProgramType(),
                subscription.billingStartDate());
        return stopFor(subscription, request, SubscriptionStatus.EXPIRED);
    }

    @Override
    public Boolean rollOver(Subscription fromSubscription, Subscription toSubscription) {
        if (!stopExpired(fromSubscription)) return false;
        BillingCycleRequest request = new BillingCycleRequest(
                toSubscription.subscriberNumber(),
                toSubscription.getProgramType(),
                fromSubscription.billingStartDate());
        return startFor(toSubscription, request);
    }

    @Override
    public Boolean stopByUser(Subscription subscription) {
        BillingCycleRequest request = new BillingCycleRequest(
                subscription.subscriberNumber(),
                subscription.getProgramType(),
                subscription.billingStartDate());
        return stopFor(subscription, request, SubscriptionStatus.SUSPENDED);
    }

    private Boolean stopFor(Subscription subscription, BillingCycleRequest request, SubscriptionStatus status) {
        BillingServiceResponse<CustomerBill> response = billingService.stopBilling(request);
        if (response.hasErrors()) {
            sendMessage(subscription, messageFor(response.getValidationErrors()));
            return false;
        }
        subscription.setStatus(status);
        sendMessage(subscription, messageFor(MessageBundle.BILLING_STOPPED));
        return true;
    }

    private Boolean startFor(Subscription subscription, BillingCycleRequest request) {
        BillingServiceResponse<CustomerBill> response = billingService.startBilling(request);
        if (response.hasErrors()) {
            sendMessage(subscription, messageFor(response.getValidationErrors()));
            return false;
        }
        String content = String.format(messageFor(MessageBundle.BILLING_SUCCESS), response.getValue().amountCharged());
        sendMessage(subscription, content);
        return true;
    }
}
