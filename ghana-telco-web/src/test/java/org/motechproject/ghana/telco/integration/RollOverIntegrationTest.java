package org.motechproject.ghana.telco.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.domain.SubscriptionStatus;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ghana.telco.domain.ProgramType.CHILDCARE;
import static org.motechproject.ghana.telco.domain.ProgramType.PREGNANCY;


public class RollOverIntegrationTest extends BaseIntegrationTest {

    @Autowired
    AllMessageCampaigns allMessageCampaigns;

    @Before
    public void setUp() {
        addSeedData();
        addAndMarkForDeletion(allProgramTypes, pregnancyProgramType);
        addAndMarkForDeletion(allProgramTypes, childCarePregnancyType);
    }

    String subscriberEmma = "9500012345";

    @Test
    public void ShouldRetainExistingChildCareSubscription_WhenRolloverOfPregnancyToChildSubscriptionHappens() throws IOException {

        Subscription pregnancySubscription = enroll(subscriberEmma, "p 06", PREGNANCY);
        Subscription childCareSubscription = enroll(subscriberEmma, "c 06", CHILDCARE);

        message(subscriberEmma, "d");
        pregnancySubscription = subscription(pregnancySubscription);
        assertEquals(SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE, pregnancySubscription.getStatus());
        assertCampaignSchedule(pregnancySubscription);

        childCareSubscription = subscription(childCareSubscription);
        assertEquals(SubscriptionStatus.ACTIVE, subscription(childCareSubscription).getStatus());
        assertCampaignSchedule(pregnancySubscription);

        message(subscriberEmma, "e");
        assertEquals(SubscriptionStatus.EXPIRED, subscription(pregnancySubscription).getStatus());
        assertIfCampaignScheduleIsStopped(pregnancySubscription);

        assertEquals(SubscriptionStatus.ACTIVE, subscription(childCareSubscription).getStatus());
        assertCampaignSchedule(childCareSubscription);
    }
    
    @Test
    public void ShouldSwitchToNewChildcareFromPendingPregnancySubscription_AndStopTheExistingChildCareSubscription() throws IOException {

        Subscription pregnancySubscription = enroll(subscriberEmma, "p 06", PREGNANCY);
        Subscription childCareSubscription = enroll(subscriberEmma, "c 06", CHILDCARE);

        message(subscriberEmma, "d");
        pregnancySubscription = subscription(pregnancySubscription);
        assertEquals(SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE, pregnancySubscription.getStatus());
        assertCampaignSchedule(pregnancySubscription);

        childCareSubscription = subscription(childCareSubscription);
        assertEquals(SubscriptionStatus.ACTIVE, subscription(childCareSubscription).getStatus());
        assertCampaignSchedule(pregnancySubscription);

        message(subscriberEmma, "n");
        Subscription newChildCareSubscription = subscription(subscriberEmma, CHILDCARE);
        assertEquals(SubscriptionStatus.ROLLED_OFF, subscription(pregnancySubscription).getStatus());
        assertEquals(SubscriptionStatus.ACTIVE, newChildCareSubscription.getStatus());
        assertIfCampaignScheduleIsStopped(pregnancySubscription);
        assertCampaignSchedule(newChildCareSubscription);

        assertEquals(SubscriptionStatus.EXPIRED, subscription(childCareSubscription).getStatus());
    }

    @After
    public void after() { super.cleanData(); }
}
