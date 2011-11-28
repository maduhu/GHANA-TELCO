package org.motechproject.ghana.mtn.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ghana.mtn.domain.ProgramType.CHILDCARE;
import static org.motechproject.ghana.mtn.domain.ProgramType.PREGNANCY;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.ACTIVE;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.SUSPENDED;


public class StopIntegrationTest extends BaseIntegrationTest {

    @Autowired
    AllMessageCampaigns allMessageCampaigns;

    @Before
    public void setUp() {
        addSeedData();
        addAndMarkForDeletion(allProgramTypes, pregnancyProgramType);
        addAndMarkForDeletion(allProgramTypes, childCarePregnancyType);
    }

    String subscriberEmma ="9500012345";

    @Test
    public void ShouldStopUserProgramWhoHasRolloverToChildCareIfUserSendsStop() throws IOException {

        Subscription pregnancySubscription = enroll(subscriberEmma, "p 08", PREGNANCY);
        pregnancySubscription = subscription(pregnancySubscription);
        assertCampaignSchedule(pregnancySubscription);

        message(subscriberEmma, "d");
        Subscription childCareSubscription = subscription(pregnancySubscription.subscriberNumber(), CHILDCARE);
        assertCampaignSchedule(childCareSubscription);
        assertIfCampaignScheduleIsStopped(pregnancySubscription);

        message(subscriberEmma, "stop");
        assertEquals(SUSPENDED, subscription(childCareSubscription).getStatus());
        assertIfCampaignScheduleIsStopped(childCareSubscription);
    }

    @Test
    public void ShouldStopUserProgramIfUserSendsStop() throws IOException {

        Subscription pregnancySubscription = enroll(subscriberEmma, "p 08", PREGNANCY);
        Subscription childCareSubscription = enroll(subscriberEmma, "c 08", CHILDCARE);

        message(subscriberEmma, "stop p");
        assertEquals(SUSPENDED, subscription(pregnancySubscription).getStatus());
        assertIfCampaignScheduleIsStopped(pregnancySubscription);

        message(subscriberEmma, "stop c");
        assertEquals(SUSPENDED, subscription(childCareSubscription).getStatus());
        assertIfCampaignScheduleIsStopped(childCareSubscription);
    }

    @Test
    public void ShouldNotStopUserProgramIfUserHas2SubscriptionsAndSendsStopWithoutMentioningProgram() throws IOException {

        Subscription pregnancySubscription = enroll(subscriberEmma, "p 08", PREGNANCY);
        Subscription childCareSubscription = enroll(subscriberEmma, "c 08", CHILDCARE);

        message(subscriberEmma, "stop");

        assertEquals(ACTIVE, subscription(pregnancySubscription).getStatus());
        assertCampaignSchedule(pregnancySubscription);

        assertEquals(ACTIVE, subscription(childCareSubscription).getStatus());
        assertCampaignSchedule(childCareSubscription);

        message(subscriberEmma, "stop c");
        assertEquals(SUSPENDED, subscription(childCareSubscription).getStatus());
        assertIfCampaignScheduleIsStopped(childCareSubscription);

        assertEquals(ACTIVE, subscription(pregnancySubscription).getStatus());
        assertCampaignSchedule(pregnancySubscription);
    }

    @After
    public void after() { super.cleanData(); }
}
