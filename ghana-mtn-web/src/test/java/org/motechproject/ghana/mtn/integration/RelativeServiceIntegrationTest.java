package org.motechproject.ghana.mtn.integration;

import org.ektorp.DbPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.billing.domain.BillAccount;
import org.motechproject.ghana.mtn.billing.domain.MTNMockUser;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.matchers.SubscriberMatcher;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;


public class RelativeServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    AllMessageCampaigns allMessageCampaigns;
    private MTNMockUser validMtnMockUser = new MTNMockUser("0950001234", new Money(10D));

    @Before
    public void setUp() {
        addAndMarkForDeletion(allProgramTypes, pregnancyProgramType);
        addAndMarkForDeletion(allProgramTypes, childCarePregnancyType);
        addAndMarkForDeletion(allShortCodes, shortCode);
        addAndMarkForDeletion(allMtnMock, mtnMockUser);
        addAndMarkForDeletion(allMtnMock, validMtnMockUser);
    }

    @Test
    public void ShouldEnrollRelativeSubscriber() throws IOException {
        String mobileNumber = "0950001234";
        String shortCode = "P";
        int week = 30;
        String inputMessage = "R " + mobileNumber + " " + shortCode + " " + week;
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest(inputMessage, "12345");

        subscriptionController.handle(subscriptionRequest);

        List<Subscription> subscriptions = allSubscriptions.getAll();
        List<Subscriber> subscribers = allSubscribers.getAll();
        ProgramType programType = allProgramTypes.findByCampaignShortCode(shortCode);
        Subscription subscription = subscriptions.get(0);

        assertThat(subscriptions.size(), is(1));
        assertThat(subscription.getProgramType(), new ProgramTypeMatcher(programType));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(week));
        assertThat(subscription.getStatus(), is(SubscriptionStatus.ACTIVE));
        assertThat(subscribers.size(), is(1));
        assertThat(subscription.getSubscriber(), new SubscriberMatcher(subscribers.get(0)));
    }

    @Test
    public void shouldThrowInvalidNumberMessageWhenTheSubscriberNumberIsNotInMTNFormat() {
        String mobileNumber = "9510001234";
        String shortCode = "P";
        int week = 30;
        String inputMessage = "R " + mobileNumber + " " + shortCode + " " + week;
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest(inputMessage, "12345");

        subscriptionController.handle(subscriptionRequest);

        List<Subscription> subscriptions = allSubscriptions.getAll();
        List<Subscriber> subscribers = allSubscribers.getAll();

        assertThat(subscriptions.size(), is(0));
        assertThat(subscribers.size(), is(0));

        assertMessageSentToUser("Invalid Phone Number");
    }

    @Test
    public void ShouldSendFailureResponseForInvalidMessage() throws IOException {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("P25", "1234567890");
        subscriptionController.handle(subscriptionRequest);
        assertFalse(couchDbInstance.checkIfDbExists(new DbPath(dbConnector.getDatabaseName() + "/Subscription")));
    }

    @Test
    public void ShouldCheckTheCampaignProgramJsonForKeysDefinedInProgramType() throws IOException {
        assertNotNull(allMessageCampaigns.get(IProgramType.PREGNANCY));
        assertNotNull(allMessageCampaigns.get(IProgramType.CHILDCARE));
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
