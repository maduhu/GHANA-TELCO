package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.dto.DefaultedBillingRequest;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class BillingServiceMediator extends BaseSubscriptionProcess {

    private BillingService billingService;
    private AllSubscriptions allSubscriptions;

    @Autowired
    public BillingServiceMediator(SMSService smsService, MessageBundle messageBundle, BillingService billingService, AllSubscriptions allSubscriptions) {
        super(smsService, messageBundle);
        this.billingService = billingService;
        this.allSubscriptions = allSubscriptions;
    }

    public void chargeFeeAndHandleResponse(Subscription subscription) {
        BillingServiceResponse<CustomerBill> response = chargeFee(subscription);
        if (response.hasErrors()) {
            sendMessage(subscription, messageFor(response.getValidationErrors()));
            createDefaultedBillingDailySchedule(subscription, response);
            updateSubscriptionStatus(subscription);
        } else {
            sendMessage(subscription, format(messageFor(MessageBundle.BILLING_SUCCESS), response.getValue().amountChargedWithCurrency()));
        }
    }

    public BillingServiceResponse chargeFeeForDefaultedSubscription(Subscription subscription) {
        BillingServiceResponse serviceResponse = chargeFee(subscription);
        if (serviceResponse.hasErrors()) {

        }
        return serviceResponse;
    }

    private void updateSubscriptionStatus(Subscription subscription) {
        subscription.setStatus(SubscriptionStatus.PAYMENT_DEFAULT);
        allSubscriptions.update(subscription);
    }

    private BillingServiceResponse<CustomerBill> chargeFee(Subscription subscription) {
        return billingService.chargeProgramFee(new BillingServiceRequest(subscription.subscriberNumber(), subscription.getProgramType()));
    }

    private void createDefaultedBillingDailySchedule(Subscription subscription, BillingServiceResponse response) {
        if (response.getValidationErrors().contains(ValidationError.INSUFFICIENT_FUNDS)) {
            WallTime frequency = new WallTime(7, WallTimeUnit.Day);
            DefaultedBillingRequest billingRequest = new DefaultedBillingRequest(subscription.subscriberNumber(), subscription.getProgramType(),
                    subscription.getCycleStartDate(), frequency, subscription.getCycleEndDate());
            billingService.startDefaultedBillingSchedule(billingRequest);
        }
    }
}
