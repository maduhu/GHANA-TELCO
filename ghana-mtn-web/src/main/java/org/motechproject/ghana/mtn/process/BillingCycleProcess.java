package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRollOverRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

@Component
public class BillingCycleProcess extends BaseSubscriptionProcess implements ISubscriptionFlowProcess {
    private BillingService billingService;
    private AllSubscriptions allSubscriptions;

    @Autowired
    public BillingCycleProcess(BillingService billingService, SMSService smsService, MessageBundle messageBundle, AllSubscriptions allSubscriptions) {
        super(smsService, messageBundle);
        this.billingService = billingService;
        this.allSubscriptions = allSubscriptions;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        BillingCycleRequest request = new BillingCycleRequest(
                subscription.subscriberNumber(),
                subscription.getProgramType(),
                subscription.billingStartDate());
        return startFor(subscription, request, MessageBundle.BILLING_SUCCESS);
    }

    @Override
    public Boolean stopExpired(Subscription subscription) {
        BillingCycleRequest request = new BillingCycleRequest(
                subscription.subscriberNumber(),
                subscription.getProgramType(),
                subscription.billingStartDate());
        return stopFor(subscription, request, SubscriptionStatus.EXPIRED, MessageBundle.BILLING_STOPPED);
    }

    @Override
    public Boolean rollOver(Subscription fromSubscription, Subscription toSubscription) {
        if (WAITING_FOR_ROLLOVER_RESPONSE.equals(fromSubscription.getStatus())) {
            billingService.stopBilling(new BillingCycleRequest(fromSubscription.subscriberNumber(), fromSubscription.getProgramType(), null));
            return true;
        }

        BillingCycleRequest fromRequest = new BillingCycleRequest(fromSubscription.subscriberNumber(),
                fromSubscription.getProgramType(), fromSubscription.billingStartDate());

        BillingCycleRequest toRequest = new BillingCycleRequest(toSubscription.subscriberNumber(),
                toSubscription.getProgramType(), fromSubscription.billingStartDate());

        return handleResponse(toSubscription, billingService.rollOverBilling(new BillingCycleRollOverRequest(fromRequest, toRequest)), MessageBundle.BILLING_ROLLOVER);
    }

    private boolean handleResponse(Subscription toSubscription, BillingServiceResponse response, String successMsg) {
        if (response.hasErrors()) {
            sendMessage(toSubscription, messageFor(response.getValidationErrors()));
            return false;
        }
        sendMessage(toSubscription, messageFor(successMsg));
        return true;
    }

    @Override
    public Boolean retainExistingChildCare(Subscription subscription) {
        return true;
    }

    @Override
    public Boolean stopByUser(Subscription subscription) {
        BillingCycleRequest request = new BillingCycleRequest(
                subscription.subscriberNumber(),
                subscription.getProgramType(),
                subscription.billingStartDate());
        return stopFor(subscription, request, SubscriptionStatus.SUSPENDED, MessageBundle.BILLING_STOPPED);
    }

    private Boolean stopFor(Subscription subscription, BillingCycleRequest request, SubscriptionStatus status, String msgKey) {
        BillingServiceResponse response = billingService.stopBilling(request);
        if (response.hasErrors()) {
            sendMessage(subscription, messageFor(response.getValidationErrors()));
            return false;
        }
        subscription.setStatus(status);
        sendMessage(subscription, messageFor(msgKey));
        return true;
    }

    private Boolean startFor(Subscription subscription, BillingCycleRequest request, String msgKey) {
        BillingServiceResponse<CustomerBill> response = billingService.startBilling(request);
        if (response.hasErrors()) {
            sendMessage(subscription, messageFor(response.getValidationErrors()));
            return false;
        }
        String content = String.format(messageFor(msgKey), response.getValue().amountCharged());
        sendMessage(subscription, content);
        return true;
    }
}
