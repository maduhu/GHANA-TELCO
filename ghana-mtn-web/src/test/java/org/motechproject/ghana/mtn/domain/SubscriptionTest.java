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
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SubscriptionTest {
    DateUtils dateUtils = new DateUtils();

    @Before
    public void setUp() {
        dateUtils = spy(dateUtils);
    }

    @Test
    public void shouldReturnCurrentRunningWeekForSubscriptionProgramBasedOnSundayAsStartOfWeek() {

        DateTime wedFeb2 = date(2012, 2, 2);
        DateTime satFeb5 = date(2012, 2, 5);
        DateTime sunFeb6 = date(2012, 2, 6);
        DateTime wedFeb24 = date(2012, 2, 24);

        Subscription registeredOn_wedFeb2 = subscription("9999933333", wedFeb2, new Week(6), programType("Pregnancy"));
        Subscription registeredOn_satFeb5 = subscription("9999933333", satFeb5, new Week(6), programType("Child"));
        Subscription registeredOn_sunFeb6 = subscription("9999933333", sunFeb6, new Week(8), programType("Child"));
        Subscription registeredOn_wedFeb24 = subscription("9999933333", wedFeb24, new Week(9), programType("Child"));

        mockCurrentDate(date(2012, 2, 5)); // sat
        assertWeek(new Week(6), registeredOn_wedFeb2.currentWeek());
        assertWeek(new Week(6), registeredOn_satFeb5.currentWeek());

        mockCurrentDate(date(2012, 2, 6)); // sun
        assertWeek(new Week(7), registeredOn_wedFeb2.currentWeek());
        assertWeek(new Week(7), registeredOn_satFeb5.currentWeek());
        assertWeek(new Week(8), registeredOn_sunFeb6.currentWeek());

        mockCurrentDate(date(2012, 2, 19)); // sat
        assertWeek(new Week(8), registeredOn_wedFeb2.currentWeek());
        assertWeek(new Week(8), registeredOn_satFeb5.currentWeek());
        assertWeek(new Week(9), registeredOn_sunFeb6.currentWeek());

        mockCurrentDate(date(2012, 2, 27)); // sun
        assertWeek(new Week(10), registeredOn_wedFeb2.currentWeek());
        assertWeek(new Week(10), registeredOn_satFeb5.currentWeek());
        assertWeek(new Week(11), registeredOn_sunFeb6.currentWeek());
        assertWeek(new Week(10), registeredOn_wedFeb24.currentWeek());
    }
    
    @Test
    public void shouldReturnCurrentDay() {
        Subscription sub1 = subscription("9999933333", new DateTime(2012, 2, 2, 10, 0), new Week(6), programType("Pregnancy"));
        mockCurrentDate(new DateTime(2012, 1, 1, 1, 1));
        assertEquals(Day.SUNDAY, sub1.currentDay());

    }

    private ProgramType programType(String programName) {
        return new ProgramType().setProgramName(programName);
    }

    private void assertWeek(Week w1, Week w2) {
        assertEquals(w1.getNumber(), w2.getNumber());
    }

    private DateUtils mockCurrentDate(DateTime dateTime) {
        when(dateUtils.now()).thenReturn(dateTime);
        return dateUtils;
    }

    private Subscription subscription(String mobileNumber, DateTime registeredDate, Week startWeek, ProgramType program) {
        Subscription subscription = new SubscriptionBuilder().withRegistrationDate(registeredDate).withStartWeekAndDay(new WeekAndDay(startWeek, Day.MONDAY))
                .withStatus(SubscriptionStatus.ACTIVE).withSubscriber(new Subscriber(mobileNumber))
                .withType(program).build();
        ReflectionTestUtils.setField(subscription, "dateUtils", dateUtils);
        return subscription;
    }

     @Test
    public void shouldCheckIfMessageAlreadySent() {
        Subscription sub1 = new Subscription();
        assertFalse(sub1.alreadySent(new ProgramMessage()));
     }

    private DateTime date(int year, int month, int date) {
        return new DateTime(year, month, date, 10, 10);
    }
}
