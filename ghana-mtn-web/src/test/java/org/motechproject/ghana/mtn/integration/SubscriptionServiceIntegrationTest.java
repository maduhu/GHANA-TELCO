package org.motechproject.ghana.mtn.integration;

import org.ektorp.DbPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ghana.mtn.billing.domain.BillAccount;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.matchers.SubscriberMatcher;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@Ignore
public class SubscriptionServiceIntegrationTest extends BaseIntegrationTest {

    @Before
    public void setUp() {
        addAndMarkForDeletion(allProgramTypes, pregnancyProgramType);
        addAndMarkForDeletion(allProgramTypes, childCarePregnancyType);
        addAndMarkForDeletion(allShortCodes, shortCode);
    }

    @Test
    public void ShouldEnrollSubscriber() throws IOException {
        String shortCode = "P";
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest(shortCode + " 25", "9500012345");

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
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("P25", "1234567890");
        subscriptionController.handle(subscriptionRequest);
        assertFalse(couchDbInstance.checkIfDbExists(new DbPath(dbConnector.getDatabaseName() + "/Subscription")));
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