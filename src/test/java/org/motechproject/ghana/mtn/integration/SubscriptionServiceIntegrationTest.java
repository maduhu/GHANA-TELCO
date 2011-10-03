package org.motechproject.ghana.mtn.integration;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.mtn.BaseIntegrationTest;
import org.motechproject.ghana.mtn.controller.SubscriptionController;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionTypeBuilder;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.matchers.SubscriberMatcher;
import org.motechproject.ghana.mtn.matchers.SubscriptionTypeMatcher;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptionTypes;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class SubscriptionServiceIntegrationTest extends BaseIntegrationTest{
    private Logger log = Logger.getLogger(SubscriptionServiceIntegrationTest.class);

    @Autowired
    private SubscriptionController subscriptionController;
    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    private AllSubscribers allSubscribers;
    @Autowired
    private AllSubscriptionTypes allSubscriptionTypes;

    public final SubscriptionType childCarePregnancyType = new SubscriptionTypeBuilder().withMinWeek(1).withMaxWeek(52).withProgramName("Child Care").withShortCode("C").withShortCode("c").build();
    public final SubscriptionType pregnancySubscriptionType = new SubscriptionTypeBuilder().withMinWeek(5).withMaxWeek(35).withProgramName("Pregnancy").withShortCode("P").withShortCode("p").build();

    @Before
    public void setUp() {
        createSubscriptionTypes();
    }

    private void createSubscriptionTypes() {
        allSubscriptionTypes.add(pregnancySubscriptionType);
        allSubscriptionTypes.add(childCarePregnancyType);
    }

    @Test
    public void ShouldEnrollSubscriber() throws IOException {
        String shortCode = "P";
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest(shortCode + " 25", "1234567890");

        String expectedResponse = SubscriptionController.JSON_PREFIX
                + String.format(MessageBundle.SUCCESSFUL_ENROLLMENT_MESSAGE_FORMAT, "Pregnancy") + SubscriptionController.JSON_SUFFIX;

        subscriptionController.enroll(subscriptionRequest, response);

        List<Subscription> subscriptions = allSubscriptions.getAll();
        Subscription subscription = subscriptions.get(0);

        List<Subscriber> subscribers = allSubscribers.getAll();
        SubscriptionType subscriptionType = allSubscriptionTypes.findByCampaignShortCode(shortCode);

        assertThat(response.getContentType(), is(SubscriptionController.CONTENT_TYPE_JSON));
        assertThat(response.getContentAsString(), is(expectedResponse));
        assertThat(subscriptions.size(), is(1));

        assertThat(subscription.getSubscriptionType(), new SubscriptionTypeMatcher(subscriptionType));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(25));
        assertThat(subscription.getStatus(), is(SubscriptionStatus.ACTIVE));

        assertThat(subscribers.size(), is(1));
        assertThat(subscription.getSubscriber(), new SubscriberMatcher(subscribers.get(0)));
    }

    @Test
    public void ShouldSendFailureResponseForInvalidMessage() throws IOException {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("P25", "1234567890");

        subscriptionController.enroll(subscriptionRequest, response);

        assertThat(response.getContentAsString(), is(SubscriptionController.JSON_PREFIX + MessageBundle.FAILURE_ENROLLMENT_MESSAGE + SubscriptionController.JSON_SUFFIX));
        assertFalse(couchDbInstance.checkIfDbExists(new DbPath(dbConnector.getDatabaseName() + "/Subscription")));
    }

    private SubscriptionRequest createSubscriptionRequest(String inputMessage, String subscriberNumber) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setInputMessage(inputMessage);
        subscriptionRequest.setSubscriberNumber(subscriberNumber);
        return subscriptionRequest;
    }

    @After
    public void after() {
        markForDeletion(pregnancySubscriptionType, childCarePregnancyType);
        super.after();
        remove(allSubscriptions.getAll());
        remove(allSubscribers.getAll());
        removeAllQuartzJobs();
    }

}