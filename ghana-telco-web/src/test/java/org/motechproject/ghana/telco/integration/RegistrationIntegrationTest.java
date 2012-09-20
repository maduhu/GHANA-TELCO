package org.motechproject.ghana.telco.integration;

import org.ektorp.DbPath;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.Subscriber;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.domain.SubscriptionStatus;
import org.motechproject.ghana.telco.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.telco.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.telco.matchers.SubscriberMatcher;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.sms.api.SMSRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.telco.domain.ProgramType.CHILDCARE;
import static org.motechproject.ghana.telco.domain.ProgramType.PREGNANCY;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:testApplicationContext.xml")
public class RegistrationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    AllMessageCampaigns allMessageCampaigns;
    @Autowired
    private SmsAuditService smsAuditService;

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
    public void ShouldEnrollSubscriberToChildCare() throws IOException {
        String shortCode = "C";
        SubscriptionRequest subscriptionRequest = request(shortCode + " 7", "9500012345");

        subscriptionController.handle(subscriptionRequest);

        List<Subscription> subscriptions = allSubscriptions.getAll();
        List<Subscriber> subscribers = allSubscribers.getAll();
        ProgramType programType = allProgramTypes.findByCampaignShortCode(shortCode);
        Subscription subscription = subscriptions.get(0);

        assertThat(subscriptions.size(), is(1));
        assertThat(subscription.getProgramType(), new ProgramTypeMatcher(programType));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(26));
        assertThat(subscription.getStatus(), is(SubscriptionStatus.ACTIVE));
        assertThat(subscribers.size(), is(1));
        assertThat(subscription.getSubscriber(), new SubscriberMatcher(subscribers.get(0)));
    }

    @Ignore
    public void ShouldSendFailureResponseForInvalidPregnancyRegistrationMessage() throws IOException, InterruptedException {
        String subscriberNumber = "1234567890";
        SubscriptionRequest subscriptionRequest = request("P 45", subscriberNumber);
        subscriptionController.handle(subscriptionRequest);
        assertFalse(couchDbInstance.checkIfDbExists(new DbPath(dbConnector.getDatabaseName() + "/Subscription")));
        Thread.sleep(2000);
        List<SMSRecord> smsRecords = smsAuditService.allOutboundMessagesBetween(DateTime.now().minusMinutes(1), DateTime.now());
        List<SMSRecord> subscriberOutboundMessages = filter(having(on(SMSRecord.class).getPhoneNo(), Matchers.is(subscriberNumber)), smsRecords);
        List<SMSRecord> messageAudits = sort(subscriberOutboundMessages, on(SMSRecord.class).getMessageTime(), sortComparator());
        assertThat(messageAudits.get(0).getContent(), is("Sorry we are having trouble processing your request."));
    }

    @Ignore
    public void ShouldSendFailureResponseForInvalidChildcareRegistrationMessage() throws IOException, InterruptedException {
        String subscriberNumber = "1234567891";
        SubscriptionRequest subscriptionRequest = request("C 13", subscriberNumber);
        subscriptionController.handle(subscriptionRequest);
        assertFalse(couchDbInstance.checkIfDbExists(new DbPath(dbConnector.getDatabaseName() + "/Subscription")));
        Thread.sleep(2000);
        List<SMSRecord> smsRecords = smsAuditService.allOutboundMessagesBetween(DateTime.now().minusMinutes(1), DateTime.now());
        List<SMSRecord> subscriberOutboundMessages = filter(having(on(SMSRecord.class).getPhoneNo(), Matchers.is(subscriberNumber)), smsRecords);
        List<SMSRecord> messageAudits = sort(subscriberOutboundMessages, on(SMSRecord.class).getMessageTime(), sortComparator());
        assertThat(messageAudits.get(0).getContent(), is("Sorry we are having trouble processing your request."));
    }

    @Test
    public void ShouldCheckTheCampaignProgramJsonForKeysDefinedInProgramType() throws IOException {
        assertNotNull(allMessageCampaigns.get(PREGNANCY));
        assertNotNull(allMessageCampaigns.get(CHILDCARE));
    }

    private Comparator<DateTime> sortComparator() {
        return new Comparator<DateTime>() {
            @Override
            public int compare(DateTime o1, DateTime o2) {
                return o2.compareTo(o1);
            }
        };
    }

    @After
    public void after() {
        super.cleanData();
    }
}
