package org.motechproject.ghana.mtn.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ghana.mtn.domain.IProgramType.CHILDCARE;
import static org.motechproject.ghana.mtn.domain.IProgramType.PREGNANCY;
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
        addAndMarkForDeletion(allMtnMock, mtnMockUser);
    }

    String subscriberEmma = mtnMockUser.getMobileNumber();

    @Test
    public void ShouldStopUserProgramWhoHasRolloverToChildCareIfUserSendsStop() throws IOException {

        Subscription pregnancySubscription = enroll(subscriberEmma, "p 08", PREGNANCY);
        pregnancySubscription = subscription(pregnancySubscription);
        assertBillingScheduleAndAccount(pregnancySubscription);
        assertCampaignSchedule(pregnancySubscription);

        message(subscriberEmma, "d");
        Subscription childCareSubscription = subscription(pregnancySubscription.subscriberNumber(), CHILDCARE);
        assertBillingSchedule(childCareSubscription);
        assertCampaignSchedule(childCareSubscription);
        assertIfBillingScheduleIsStopped(pregnancySubscription);
        assertIfCampaignScheduleIsStopped(pregnancySubscription);

        message(subscriberEmma, "stop");
        assertEquals(SUSPENDED, subscription(childCareSubscription).getStatus());
        assertIfBillingScheduleIsStopped(childCareSubscription);
        assertIfCampaignScheduleIsStopped(childCareSubscription);
    }

    @Test
    public void ShouldStopUserProgramIfUserSendsStop() throws IOException {

        Subscription pregnancySubscription = enroll(subscriberEmma, "p 08", PREGNANCY);
        Subscription childCareSubscription = enroll(subscriberEmma, "c 08", CHILDCARE);

        message(subscriberEmma, "stop p");
        assertEquals(SUSPENDED, subscription(pregnancySubscription).getStatus());
        assertIfBillingScheduleIsStopped(pregnancySubscription);
        assertIfCampaignScheduleIsStopped(pregnancySubscription);

        message(subscriberEmma, "stop c");
        assertEquals(SUSPENDED, subscription(childCareSubscription).getStatus());
        assertIfBillingScheduleIsStopped(childCareSubscription);
        assertIfCampaignScheduleIsStopped(childCareSubscription);
    }

    @Test
    public void ShouldNotStopUserProgramIfUserHas2SubscriptionsAndSendsStopWithoutMentioningProgram() throws IOException {

        Subscription pregnancySubscription = enroll(subscriberEmma, "p 08", PREGNANCY);
        Subscription childCareSubscription = enroll(subscriberEmma, "c 08", CHILDCARE);

        message(subscriberEmma, "stop");

        assertEquals(ACTIVE, subscription(pregnancySubscription).getStatus());
        assertBillingScheduleAndAccount(pregnancySubscription);
        assertCampaignSchedule(pregnancySubscription);

        assertEquals(ACTIVE, subscription(childCareSubscription).getStatus());
        assertBillingScheduleAndAccount(childCareSubscription);
        assertCampaignSchedule(childCareSubscription);

        message(subscriberEmma, "stop c");
        assertEquals(SUSPENDED, subscription(childCareSubscription).getStatus());
        assertIfBillingScheduleIsStopped(childCareSubscription);
        assertIfCampaignScheduleIsStopped(childCareSubscription);

        assertEquals(ACTIVE, subscription(pregnancySubscription).getStatus());
        assertBillingScheduleAndAccount(pregnancySubscription);
        assertCampaignSchedule(pregnancySubscription);
    }

    @After
    public void after() { super.cleanData(); }
}
