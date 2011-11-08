package org.motechproject.ghana.mtn.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.motechproject.ghana.mtn.billing.domain.BillAccount;
import org.motechproject.ghana.mtn.billing.domain.MTNMockUser;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.vo.Money;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest(System.class)
@Ignore
public class SubscriptionBillingIntegrationTest extends BaseIntegrationTest {

//    @Rule
//    public PowerMockRule rule = new PowerMockRule();

    public static final String MOBILE_NUMBER_WITH_LESS_FUND = "0950012345";
    private MTNMockUser userWithLessFund = new MTNMockUser(MOBILE_NUMBER_WITH_LESS_FUND, new Money(0.60D));

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
