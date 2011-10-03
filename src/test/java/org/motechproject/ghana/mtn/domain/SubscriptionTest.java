package org.motechproject.ghana.mtn.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.utils.DateUtils;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SubscriptionTest {
    DateUtils dateUtils = new DateUtils();

    @Before
    public void setUp() {
        dateUtils = spy(dateUtils);
    }

    @Test
    public void shouldReturnCurrentRunningWeekForPregnancySubscriptionProgram() {
        Subscription sub1 = subscription("9999933333", new DateTime(2012, 2, 2, 10, 0), new Week(6), subscriptionType("Pregnancy"));
        Subscription sub2 = subscription("9999933333", new DateTime(2012, 2, 26, 10, 10), new Week(7), subscriptionType("Child Care"));

        mockCurrentDate(new DateTime(2012, 2, 27, 23, 2));
        assertWeek(new Week(9), sub1.currentWeek());
        assertWeek(new Week(7), sub2.currentWeek());

        mockCurrentDate(new DateTime(2012, 5, 6, 2, 2));
        assertWeek(new Week(19), sub1.currentWeek());
        assertWeek(new Week(16), sub2.currentWeek());
    }

    @Test
    public void shouldReturnCurrentDay() {
        Subscription sub1 = subscription("9999933333", new DateTime(2012, 2, 2, 10, 0), new Week(6), subscriptionType("Pregnancy"));
        mockCurrentDate(new DateTime(2012, 1, 1, 1, 1));
        assertEquals(Day.SUNDAY, sub1.currentDay());

    }

    private SubscriptionType subscriptionType(String programName) {
        return new SubscriptionType().setProgramName(programName);
    }

    private void assertWeek(Week w1, Week w2) {
        assertEquals(w1.getNumber(), w2.getNumber());
    }

    private DateUtils mockCurrentDate(DateTime dateTime) {
        when(dateUtils.now()).thenReturn(dateTime);
        return dateUtils;
    }

    private Subscription subscription(String mobileNumber, DateTime registeredDate, Week startWeek, SubscriptionType program) {
        Subscription subscription = new SubscriptionBuilder().withRegistrationDate(registeredDate).withStartWeekAndDay(new WeekAndDay(startWeek, Day.MONDAY))
                .withStatus(SubscriptionStatus.ACTIVE).withSubscriber(new Subscriber(mobileNumber))
                .withType(program).build();
        ReflectionTestUtils.setField(subscription, "dateUtils", dateUtils);
        return subscription;
    }

}
