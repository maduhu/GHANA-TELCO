package org.motechproject.ghana.mtn.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.billing.domain.BillAccount;
import org.motechproject.ghana.mtn.billing.domain.MTNMockUser;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.eventhandler.BillingEventHandler;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.EXTERNAL_ID_KEY;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.MONTHLY_BILLING_SCHEDULE_SUBJECT;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.PROGRAM_KEY;

public class SubscriptionBillingIntegrationTest extends BaseIntegrationTest {

    public static final String MOBILE_NUMBER_WITH_LESS_FUND = "0950012345";
    private MTNMockUser userWithLessFund = new MTNMockUser(MOBILE_NUMBER_WITH_LESS_FUND, new Money(0.60D));
    @Autowired
    private BillingEventHandler billingEventHandler;

    @Before
    public void setUp() {
        addSeedData();
        addAndMarkForDeletion(allProgramTypes, pregnancyProgramType);
        addAndMarkForDeletion(allProgramTypes, childCarePregnancyType);
        addAndMarkForDeletion(allMtnMock, mtnMockUser);
        addAndMarkForDeletion(allMtnMock, userWithLessFund);
    }

    @Test
    public void shouldStartDefaultedDailyBillingScheduleAndRunFor7Days() {
        Subscription subscription = enroll(MOBILE_NUMBER_WITH_LESS_FUND, "P 25", IProgramType.PREGNANCY);
        assertMonthlyBillingSchedule(subscription);
        triggerMonthlyBillingSchedule();
        assertDailyBillingSchedule(subscription);
        assertWeeklyBillingSchedule(subscription);
    }

    private void triggerMonthlyBillingSchedule() {
        billingEventHandler.chargeCustomer(createMotechEvent());
    }

    private MotechEvent createMotechEvent() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EXTERNAL_ID_KEY, MOBILE_NUMBER_WITH_LESS_FUND);
        params.put(PROGRAM_KEY, IProgramType.PREGNANCY);
        return new MotechEvent(MONTHLY_BILLING_SCHEDULE_SUBJECT, params);
    }

    @After
    public void after() {
        super.after();
        remove(allSubscriptions.getAll());
        remove(allSubscribers.getAll());
        for (BillAccount billAccount : allBillAccounts.getAll()) allBillAccounts.remove(billAccount);
        removeAllQuartzJobs();
    }
}
