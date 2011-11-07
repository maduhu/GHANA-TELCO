package org.motechproject.ghana.mtn.integration;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.motechproject.ghana.mtn.billing.domain.BillAccount;
import org.motechproject.ghana.mtn.billing.domain.MTNMockUser;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.vo.Money;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(System.class)
@Ignore
public class SubscriptionBillingIntegrationTest extends BaseIntegrationTest {

    public static final String MOBILE_NUMBER_WITH_LESS_FUND = "0950012345";
    private MTNMockUser userWithLessFund = new MTNMockUser(MOBILE_NUMBER_WITH_LESS_FUND, new Money(0.60D));

    @Test
    public void shouldStartDefaultedDailyBillingScheduleAndRunFor7Days() {
        Subscription subscription = enroll(MOBILE_NUMBER_WITH_LESS_FUND, "P 25", IProgramType.PREGNANCY);
//        assertDailyBillingSchedule(subscription);
    }

    private void mockSystemTimeTo(long time) {
        PowerMockito.spy(System.class);
        Mockito.when(System.currentTimeMillis()).thenReturn(time);
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
