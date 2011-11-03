package org.motechproject.ghana.mtn.process;

import org.joda.time.DateTime;
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
import org.motechproject.ghana.mtn.utils.DateUtils;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.ACTIVE;

@Component
public class BillingServiceMediator extends BaseSubscriptionProcess {

    private BillingService billingService;
    private AllSubscriptions allSubscriptions;
    DateUtils dateUtils = new DateUtils();

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
            createDefaultedDailyAndWeeklyBillingSchedule(subscription, response);
            updateSubscriptionStatus(subscription);
        } else {
            sendMessage(subscription, format(messageFor(MessageBundle.BILLING_SUCCESS), response.getValue().amountChargedWithCurrency()));
        }
    }

    public BillingServiceResponse chargeFeeForDefaultedSubscription(Subscription subscription) {
        BillingServiceResponse serviceResponse = chargeFee(subscription);
        if (!serviceResponse.hasErrors()) {
            subscription.setStatus(ACTIVE);
            allSubscriptions.update(subscription);
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

    private void createDefaultedDailyAndWeeklyBillingSchedule(Subscription subscription, BillingServiceResponse response) {
        if (response.getValidationErrors().contains(ValidationError.INSUFFICIENT_FUNDS)) {
            DateTime startDateForDailySchedule = dateUtils.startOfDay(DateUtil.now().dayOfMonth().addToCopy(1));
            DateTime endDateForDailySchedule = dateUtils.startOfDay(startDateForDailySchedule.dayOfMonth().addToCopy(6));

            DefaultedBillingRequest dailyBillingRequest = createDefaultedSchedule(subscription, WallTimeUnit.Day, startDateForDailySchedule, endDateForDailySchedule);
            billingService.startDefaultedBillingSchedule(dailyBillingRequest);

            createWeeklyDefaultBillingScheduleToRunAfterDailySchedule(subscription, endDateForDailySchedule);
        }
    }

    private void createWeeklyDefaultBillingScheduleToRunAfterDailySchedule(Subscription subscription, DateTime endDateForDailySchedule) {
        DateTime startDateForWeeklySchedule = dateUtils.startOfDay(endDateForDailySchedule.dayOfMonth().addToCopy(1));
        DefaultedBillingRequest weeklyBillingRequestAfterDailySchedule = createDefaultedSchedule(subscription, WallTimeUnit.Week, startDateForWeeklySchedule, subscription.getCycleEndDate());
        billingService.startDefaultedBillingSchedule(weeklyBillingRequestAfterDailySchedule);
    }

    private DefaultedBillingRequest createDefaultedSchedule(Subscription subscription, WallTimeUnit frequency, DateTime startDateForDailySchedule, DateTime endDateForDailySchedule) {
        return new DefaultedBillingRequest(subscription.subscriberNumber(), subscription.getProgramType(),
                startDateForDailySchedule, frequency, endDateForDailySchedule);
    }
}
