package org.motechproject.ghana.telco.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.telco.BaseSpringTestContext;
import org.motechproject.ghana.telco.domain.*;
import org.motechproject.ghana.telco.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.telco.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.telco.domain.vo.Week;
import org.motechproject.ghana.telco.domain.vo.WeekAndDay;
import org.motechproject.ghana.telco.matchers.SubscriberMatcher;
import org.motechproject.ghana.telco.matchers.SubscriptionMatcher;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.ghana.telco.domain.ProgramType.CHILDCARE;
import static org.motechproject.ghana.telco.domain.ProgramType.PREGNANCY;
import static org.motechproject.ghana.telco.domain.SubscriptionStatus.EXPIRED;
import static org.motechproject.ghana.telco.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;
import static org.motechproject.model.DayOfWeek.*;

public class AllSubscriptionsTest extends BaseSpringTestContext {
    @Autowired
    private AllSubscriptions allSubscriptions;
    private String mobileNumber = "1234567890";
    private Subscription subscription;
    private Subscriber subscriber1 = new Subscriber("0987654321");

    @Autowired
    private AllProgramTypes programTypes;

    ProgramType pregnancy;
    ProgramType childCare;
    private ProgramMessageCycle programMessageCycle = new ProgramMessageCycle();

    @Before
    public void setUp() {
        AllMessageCampaigns allMessageCampaigns = mock(AllMessageCampaigns.class);
        when(allMessageCampaigns.getApplicableDaysForRepeatingCampaign(anyString(), anyString())).thenReturn(asList(Monday, Wednesday, Friday));
        ReflectionTestUtils.setField(programMessageCycle, "allMessageCampaigns", allMessageCampaigns);

        programTypes.add(new ProgramTypeBuilder()
                .withProgramName("Pregnancy")
                .withShortCode("P")
                .withShortCode("p")
                .withProgramKey(PREGNANCY)
                .withMaxWeek(35).withMinWeek(5).build());
        programTypes.add(new ProgramTypeBuilder()
                .withProgramName("Child Care")
                .withShortCode("C")
                .withShortCode("c")
                .withProgramKey(CHILDCARE)
                .withMaxWeek(52).withMinWeek(1).build());

        pregnancy = programTypes.findByCampaignShortCode("P");
        childCare = programTypes.findByCampaignShortCode("C");

        subscription = new SubscriptionBuilder().withRegistrationDate(DateUtil.now())
                .withStartWeekAndDay(new WeekAndDay(new Week(6), Monday)).withStatus(SubscriptionStatus.ACTIVE)
                .withSubscriber(new Subscriber(mobileNumber))
                .withType(pregnancy).build();
        subscription.updateCycleInfo(programMessageCycle);
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
        ProgramType programType = new ProgramTypeBuilder()
                .withShortCode("P").withProgramName("Pregnancy").withProgramKey(ProgramType.PREGNANCY).withMinWeek(5).withMaxWeek(35).build();
        Subscription subscription = new SubscriptionBuilder().withRegistrationDate(new DateTime())
                .withStartWeekAndDay(new WeekAndDay(new Week(6), Monday)).withStatus(SubscriptionStatus.ACTIVE)
                .withSubscriber(subscriber1)
                .withType(programType).build();
        subscription.updateCycleInfo(programMessageCycle);
        allSubscriptions.add(subscription);

        List<Subscription> subscriptions = allSubscriptions.getAllActiveSubscriptionsForSubscriber(mobileNumber);

        assertTrue(!subscriptions.isEmpty());
        assertTrue(subscriptions.size() == 1);
        assertThat(subscriptions.get(0).getSubscriber(), new SubscriberMatcher(mobileNumber));

        allSubscriptions.remove(subscription);
    }

    @Test
    public void shouldFetchSubscriptionBasedOnMobileNumberAndEnrolledProgram() {
        String user1Mobile = "9999933333";
        Subscription pregnancyProgramForUser1 = subscription(user1Mobile, new DateTime(2012, 2, 2, 0, 0), new Week(6), pregnancy).build().updateCycleInfo(programMessageCycle);
        Subscription childCareForUser1 = subscription(user1Mobile, new DateTime(2012, 2, 3, 0, 0), new Week(7), childCare).build().updateCycleInfo(programMessageCycle);
        allSubscriptions.add(pregnancyProgramForUser1);
        allSubscriptions.add(childCareForUser1);

        Subscription pregnancyProgramForUser2 = subscription("987654321", new DateTime(2012, 2, 3, 0, 0), new Week(7), pregnancy).withStatus(EXPIRED).build().updateCycleInfo(programMessageCycle);
        allSubscriptions.add(pregnancyProgramForUser2);

        Subscription actualPregnancyProgramForUsr1 = allSubscriptions.findActiveSubscriptionFor(user1Mobile, pregnancy.getProgramKey());
        Subscription actualChildCareForUsr1 = allSubscriptions.findActiveSubscriptionFor(user1Mobile, childCare.getProgramKey());
        assertEquals(pregnancyProgramForUser1.getRevision(), actualPregnancyProgramForUsr1.getRevision());
        assertEquals(childCareForUser1.getRevision(), actualChildCareForUsr1.getRevision());

        assertEquals(null, allSubscriptions.findActiveSubscriptionFor(mobileNumber, childCare.getProgramKey()));
    }

    @Test
    public void shouldFetchSubscriptionBasedOnMobileNumberAndEnrolledProgramKey() {
        String user1Mobile = "9999933333";
        Subscription pregnancyProgramForUser1 = subscription(user1Mobile, new DateTime(2012, 2, 2, 0, 0), new Week(6), pregnancy).build().updateCycleInfo(programMessageCycle);
        Subscription childCareForUser1 = subscription(user1Mobile, new DateTime(2012, 2, 3, 0, 0), new Week(7), childCare).build().updateCycleInfo(programMessageCycle);
        allSubscriptions.add(pregnancyProgramForUser1);
        allSubscriptions.add(childCareForUser1);

        Subscription pregnancyProgramForUser2 = subscription("987654321", new DateTime(2012, 2, 3, 0, 0), new Week(7), pregnancy).withStatus(EXPIRED).build().updateCycleInfo(programMessageCycle);
        allSubscriptions.add(pregnancyProgramForUser2);

        Subscription actualPregnancyProgramForUsr1 = allSubscriptions.findActiveSubscriptionFor(user1Mobile, PREGNANCY);
        Subscription actualChildCareForUsr1 = allSubscriptions.findActiveSubscriptionFor(user1Mobile, CHILDCARE);
        assertEquals(pregnancyProgramForUser1.getRevision(), actualPregnancyProgramForUsr1.getRevision());
        assertThat(pregnancyProgramForUser1, new SubscriptionMatcher(actualPregnancyProgramForUsr1));
        assertEquals(childCareForUser1.getRevision(), actualChildCareForUsr1.getRevision());
        assertThat(childCareForUser1, new SubscriptionMatcher(actualChildCareForUsr1));
    }

    @Test
    public void shouldFetchWaitingForResponseSubscriptionForASubscriber() {
        String subscriberNumber = "9999933333";
        String programKey = PREGNANCY;

        Subscription pregnancySubscription = subscription(subscriberNumber, new DateTime(2012, 2, 2, 0, 0), new Week(6), pregnancy).withStatus(WAITING_FOR_ROLLOVER_RESPONSE).build().updateCycleInfo(programMessageCycle);
        allSubscriptions.add(pregnancySubscription);

        Subscription subscription = allSubscriptions.findBy(subscriberNumber, programKey, WAITING_FOR_ROLLOVER_RESPONSE);

        assertThat(subscription, new SubscriptionMatcher(pregnancySubscription));
    }

    private SubscriptionBuilder subscription(String mobileNumber, DateTime registeredDate, Week startWeek, ProgramType program) {
        return new SubscriptionBuilder().withRegistrationDate(registeredDate).withStartWeekAndDay(new WeekAndDay(startWeek, Monday))
                .withStatus(SubscriptionStatus.ACTIVE).withSubscriber(new Subscriber(mobileNumber))
                .withType(program);
    }

    @After
    public void destroy() {
        remove(allSubscriptions.getAll());
    }
}
