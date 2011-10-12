package org.motechproject.ghana.mtn.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.BaseIntegrationTest;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.matchers.SubscriberMatcher;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.EXPIRED;

public class AllSubscriptionsTest extends BaseIntegrationTest {
    @Autowired
    private AllSubscriptions allSubscriptions;
    private String mobileNumber = "1234567890";
    private Subscription subscription;
    private Subscriber subscriber1 = new Subscriber("0987654321");

    @Autowired
    private AllProgramTypes programTypes;

    ProgramType pregnancy;
    ProgramType childCare;

    @Before
    public void setUp() {
        programTypes.add(new ProgramTypeBuilder()
                .withProgramName("Pregnancy")
                .withShortCode("P")
                .withShortCode("p")
                .withMaxWeek(35).withMinWeek(5).build());
        programTypes.add(new ProgramTypeBuilder()
                .withProgramName("Child Care")
                .withShortCode("C")
                .withShortCode("c")
                .withMaxWeek(52).withMinWeek(1).build());

        pregnancy = programTypes.findByCampaignShortCode("P");
        childCare = programTypes.findByCampaignShortCode("C");

        ProgramType programType = new ProgramTypeBuilder()
                .withShortCode("P").withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build();
        subscription = new SubscriptionBuilder().withRegistrationDate(DateUtil.now())
                .withStartWeekAndDay(new WeekAndDay(new Week(6), Day.MONDAY)).withStatus(SubscriptionStatus.ACTIVE)
                .withSubscriber(new Subscriber(mobileNumber))
                .withType(programType).build();
        subscription.updateStartCycleInfo();
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
                .withShortCode("P").withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build();
        Subscription subscription = new SubscriptionBuilder().withRegistrationDate(new DateTime())
                .withStartWeekAndDay(new WeekAndDay(new Week(6), Day.MONDAY)).withStatus(SubscriptionStatus.ACTIVE)
                        .withSubscriber(subscriber1)
                        .withType(programType).build();
        subscription.updateStartCycleInfo();
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
        Subscription pregnancyProgramForUser1 = subscription(user1Mobile, new DateTime(2012, 2, 2, 0, 0), new Week(6), pregnancy).build().updateStartCycleInfo();
        Subscription childCareForUser1 = subscription(user1Mobile, new DateTime(2012, 2, 3, 0, 0), new Week(7), childCare).build().updateStartCycleInfo();
        allSubscriptions.add(pregnancyProgramForUser1);
        allSubscriptions.add(childCareForUser1);

        Subscription pregnancyProgramForUser2 = subscription("987654321", new DateTime(2012, 2, 3, 0, 0), new Week(7), pregnancy).withStatus(EXPIRED).build().updateStartCycleInfo();
        allSubscriptions.add(pregnancyProgramForUser2);

        Subscription actualPregnancyProgramForUsr1 = allSubscriptions.findBy(user1Mobile, pregnancy.getProgramName());
        Subscription actualChildCareForUsr1 = allSubscriptions.findBy(user1Mobile, childCare.getProgramName());
        assertEquals(pregnancyProgramForUser1.getRevision(), actualPregnancyProgramForUsr1.getRevision());
        assertEquals(childCareForUser1.getRevision(), actualChildCareForUsr1.getRevision());

        assertEquals(null, allSubscriptions.findBy(mobileNumber, childCare.getProgramName()));
        remove(asList(pregnancyProgramForUser1, childCareForUser1, pregnancyProgramForUser2));
    }

    private SubscriptionBuilder subscription(String mobileNumber, DateTime registeredDate, Week startWeek, ProgramType program) {
        return new SubscriptionBuilder().withRegistrationDate(registeredDate).withStartWeekAndDay(new WeekAndDay(startWeek, Day.MONDAY))
                .withStatus(SubscriptionStatus.ACTIVE).withSubscriber(new Subscriber(mobileNumber))
                .withType(program);
    }

    @After
    public void destroy() {
        remove(allSubscriptions.getAll());
    }                                            
}
