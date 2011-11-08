package org.motechproject.ghana.mtn.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ghana.mtn.domain.IProgramType.CHILDCARE;
import static org.motechproject.ghana.mtn.domain.IProgramType.PREGNANCY;


public class RollOverIntegrationTest extends BaseIntegrationTest {

    @Autowired
    AllMessageCampaigns allMessageCampaigns;

    @Before
    public void setUp() {
        addSeedData();
        addAndMarkForDeletion(allProgramTypes, pregnancyProgramType);
        addAndMarkForDeletion(allProgramTypes, childCarePregnancyType);
        addAndMarkForDeletion(allMtnMock, mtnMockUser);
    }

    String subscriberEmma = mtnMockUser.getMobileNumber();

    @Test
    public void ShouldRetainExistingChildCareSubscription_WhenRolloverOfPregnancyToChildSubscriptionHappens() throws IOException {

        Subscription pregnancySubscription = enroll(subscriberEmma, "p 06", PREGNANCY);
        Subscription childCareSubscription = enroll(subscriberEmma, "c 06", CHILDCARE);

        message(subscriberEmma, "d");
        pregnancySubscription = subscription(pregnancySubscription);
        assertEquals(SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE, pregnancySubscription.getStatus());
        assertBillingScheduleAndAccount(pregnancySubscription);
        assertCampaignSchedule(pregnancySubscription);

        childCareSubscription = subscription(childCareSubscription);
        assertEquals(SubscriptionStatus.ACTIVE, subscription(childCareSubscription).getStatus());
        assertBillingScheduleAndAccount(childCareSubscription);
        assertCampaignSchedule(pregnancySubscription);

        message(subscriberEmma, "e");
        assertEquals(SubscriptionStatus.EXPIRED, subscription(pregnancySubscription).getStatus());
        assertIfBillingScheduleIsStopped(pregnancySubscription);
        assertIfCampaignScheduleIsStopped(pregnancySubscription);

        assertEquals(SubscriptionStatus.ACTIVE, subscription(childCareSubscription).getStatus());
        assertBillingScheduleAndAccount(childCareSubscription);
        assertCampaignSchedule(childCareSubscription);
    }
    
    @Test
    public void ShouldSwitchToNewChildcareFromPendingPregnancySubscription_AndStopTheExistingChildCareSubscription() throws IOException {

        Subscription pregnancySubscription = enroll(subscriberEmma, "p 06", PREGNANCY);
        Subscription childCareSubscription = enroll(subscriberEmma, "c 06", CHILDCARE);

        message(subscriberEmma, "d");
        pregnancySubscription = subscription(pregnancySubscription);
        assertEquals(SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE, pregnancySubscription.getStatus());
        assertBillingScheduleAndAccount(pregnancySubscription);
        assertCampaignSchedule(pregnancySubscription);

        childCareSubscription = subscription(childCareSubscription);
        assertEquals(SubscriptionStatus.ACTIVE, subscription(childCareSubscription).getStatus());
        assertBillingScheduleAndAccount(childCareSubscription);
        assertCampaignSchedule(pregnancySubscription);

        message(subscriberEmma, "n");
        Subscription newChildCareSubscription = subscription(subscriberEmma, IProgramType.CHILDCARE);
        assertEquals(SubscriptionStatus.ROLLED_OFF, subscription(pregnancySubscription).getStatus());
        assertEquals(SubscriptionStatus.ACTIVE, newChildCareSubscription.getStatus());
        assertIfBillingScheduleIsStopped(pregnancySubscription);
        assertIfCampaignScheduleIsStopped(pregnancySubscription);
        assertBillingScheduleAndAccount(newChildCareSubscription);
        assertCampaignSchedule(newChildCareSubscription);

        assertEquals(SubscriptionStatus.EXPIRED, subscription(childCareSubscription).getStatus());
    }

    @After
    public void after() { super.cleanData(); }
}
