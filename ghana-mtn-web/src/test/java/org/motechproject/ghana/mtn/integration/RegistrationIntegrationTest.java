package org.motechproject.ghana.mtn.integration;

import org.ektorp.DbPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.matchers.SubscriberMatcher;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.mtn.domain.ProgramType.CHILDCARE;
import static org.motechproject.ghana.mtn.domain.ProgramType.PREGNANCY;

public class RegistrationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    AllMessageCampaigns allMessageCampaigns;

    @Before
    public void setUp() {
        addSeedData();
        addAndMarkForDeletion(allProgramTypes, pregnancyProgramType);
        addAndMarkForDeletion(allProgramTypes, childCarePregnancyType);
    }

    @Test
    public void ShouldEnrollSubscriber() throws IOException {
        String shortCode = "P";
        SubscriptionRequest subscriptionRequest = request(shortCode + " 25", "9500012345");

        subscriptionController.handle(subscriptionRequest);

        List<Subscription> subscriptions = allSubscriptions.getAll();
        List<Subscriber> subscribers = allSubscribers.getAll();
        ProgramType programType = allProgramTypes.findByCampaignShortCode(shortCode);
        Subscription subscription = subscriptions.get(0);

        assertThat(subscriptions.size(), is(1));
        assertThat(subscription.getProgramType(), new ProgramTypeMatcher(programType));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(25));
        assertThat(subscription.getStatus(), is(SubscriptionStatus.ACTIVE));
        assertThat(subscribers.size(), is(1));
        assertThat(subscription.getSubscriber(), new SubscriberMatcher(subscribers.get(0)));
    }

    @Test
    public void ShouldSendFailureResponseForInvalidMessage() throws IOException {
        SubscriptionRequest subscriptionRequest = request("P25", "1234567890");
        subscriptionController.handle(subscriptionRequest);
        assertFalse(couchDbInstance.checkIfDbExists(new DbPath(dbConnector.getDatabaseName() + "/Subscription")));
    }
    
    @Test
    public void ShouldCheckTheCampaignProgramJsonForKeysDefinedInProgramType() throws IOException {
        assertNotNull(allMessageCampaigns.get(PREGNANCY));
        assertNotNull(allMessageCampaigns.get(CHILDCARE));
    }

    @After
    public void after() {
        super.cleanData();
    }
}
