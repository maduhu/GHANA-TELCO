package org.motechproject.ghana.mtn.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionTypeBuilder;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.matchers.SubscriberMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class AllSubscriptionsTest {
    @Autowired
    private AllSubscriptions allSubscriptions;
    private String mobileNumber = "1234567890";
    private Subscription subscription;
    private Subscriber subscriber1 = new Subscriber("0987654321");

    @Before
    public void setUp() {
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder()
                .withShortCode("P").withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build();
        subscription = new SubscriptionBuilder().withRegistrationDate(new DateTime())
                .withStartWeek(new Week(6)).withStatus(SubscriptionStatus.ACTIVE)
                .withSubscriber(new Subscriber(mobileNumber))
                .withType(subscriptionType).build();
        allSubscriptions.add(subscription);
    }

    @Test
    public void ShouldFetchListOfSubscriptionsForASubscriber() {
        List<Subscription> subscriptions = allSubscriptions.getAllActiveSubscriptionsForSubscriber(mobileNumber);

        assertTrue(!subscriptions.isEmpty());
        assertThat(subscriptions.get(0).getSubscriber(), new SubscriberMatcher(mobileNumber));
    }

    @Test
    public void ShouldFetchOnlyTheSubscriptionsThatAreActiveAndRelatedToParticularSubscriber() {
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder()
                .withShortCode("P").withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build();
        Subscription subscription = new SubscriptionBuilder().withRegistrationDate(new DateTime())
                .withStartWeek(new Week(6)).withStatus(SubscriptionStatus.ACTIVE)
                .withSubscriber(subscriber1)
                .withType(subscriptionType).build();
        allSubscriptions.add(subscription);

        List<Subscription> subscriptions = allSubscriptions.getAllActiveSubscriptionsForSubscriber(mobileNumber);

        assertTrue(!subscriptions.isEmpty());
        assertTrue(subscriptions.size() == 1);
        assertThat(subscriptions.get(0).getSubscriber(), new SubscriberMatcher(mobileNumber));

        allSubscriptions.remove(subscription);
    }

    @After
    public void destroy() {
        allSubscriptions.remove(subscription);
    }
}
