package org.motechproject.ghana.mtn.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.utils.DateUtils;
import org.motechproject.model.DayOfWeek;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SubscriptionTest {
    DateUtils dateUtils = spy(new DateUtils());

    @Before
    public void setUp() {
    }

    @Test
    public void shouldComputeEndDate_BasedOnCycleStartDate() {

        Subscription registeredOn_MonJan31 = subscription("9999933333", date(2011, 1, 31), new Week(10), programType("Pregnancy"));
        Subscription registeredOn_TueFeb1 = subscription("9999933333", date(2011, 2, 1), new Week(10), programType("Pregnancy"));
        Subscription registeredOn_WedFeb2 = subscription("9999933333", date(2011, 2, 2), new Week(10), programType("Pregnancy"));
        Subscription registeredOn_ThurFeb3 = subscription("9999933333", date(2011, 2, 3), new Week(10), programType("Pregnancy"));
        Subscription registeredOn_FriFeb4 = subscription("9999933333", date(2011, 2, 4), new Week(10), programType("Pregnancy"));
        Subscription registeredOn_SatFeb5 = subscription("9999933333", date(2011, 2, 5), new Week(10), programType("Pregnancy"));
        Subscription registeredOn_SunFeb6 = subscription("9999933333", date(2011, 2, 6), new Week(10), programType("Pregnancy"));
        Subscription registeredOn_MonFeb7 = subscription("9999933333", date(2011, 2, 7), new Week(10), programType("Pregnancy"));

        int noOfDaysForRemainingWeeks = 25 * 7;
        int daysToSaturdayForMonJan31 = 3;
        DateTime endDateForJan31 = date(2011, 2, 2).dayOfMonth().addToCopy(noOfDaysForRemainingWeeks + daysToSaturdayForMonJan31);

        assertThat(registeredOn_MonJan31.getSubscriptionEndDate(), is(endDateForJan31));
        assertThat(registeredOn_TueFeb1.getSubscriptionEndDate(), is(addDays(date(2011, 2, 2), 3 + noOfDaysForRemainingWeeks)));
        assertThat(registeredOn_WedFeb2.getSubscriptionEndDate(), is(addDays(date(2011, 2, 4), 1 + noOfDaysForRemainingWeeks)));
        assertThat(registeredOn_ThurFeb3.getSubscriptionEndDate(), is(addDays(date(2011, 2, 4), 1 + noOfDaysForRemainingWeeks)));
        assertThat(registeredOn_FriFeb4.getSubscriptionEndDate(), is(addDays(date(2011, 2, 7), 5 + noOfDaysForRemainingWeeks)));
        assertThat(registeredOn_SatFeb5.getSubscriptionEndDate(), is(addDays(date(2011, 2, 7), 5 + noOfDaysForRemainingWeeks)));
        assertThat(registeredOn_SunFeb6.getSubscriptionEndDate(), is(addDays(date(2011, 2, 7), 5 + noOfDaysForRemainingWeeks)));
        assertThat(registeredOn_MonFeb7.getSubscriptionEndDate(), is(addDays(date(2011, 2, 7), 5 + noOfDaysForRemainingWeeks)));
    }

    private DateTime addDays(DateTime date, int days) {
        return date.dayOfYear().addToCopy(days);
    }

    @Test
    public void shouldReturnCurrentDay() {
        Subscription sub1 = subscription("9999933333", new DateTime(2012, 2, 2, 10, 0), new Week(6), programType("Pregnancy"));
        mockCurrentDate(new DateTime(2012, 1, 1, 1, 1));
        assertEquals(DayOfWeek.Sunday, sub1.currentDay());

    }

    private ProgramType programType(String programName) {
        ProgramType programType = new ProgramType().setProgramName(programName);
        programType.setMinWeek(5);
        programType.setMaxWeek(35);
        return programType;
    }

    private void assertWeek(Week w1, Week w2) {
        if (w1 == null) assertNull(w2);
        else assertEquals(w1.getNumber(), w2.getNumber());
    }

    private DateUtils mockCurrentDate(DateTime dateTime) {
        when(dateUtils.now()).thenReturn(dateTime);
        return dateUtils;
    }

    private Subscription subscription(String mobileNumber, DateTime registeredDate, Week startWeek, ProgramType program) {
        Subscription subscription = new SubscriptionBuilder().withRegistrationDate(registeredDate).withStartWeekAndDay(new WeekAndDay(startWeek, DayOfWeek.Monday))
                .withStatus(SubscriptionStatus.ACTIVE).withSubscriber(new Subscriber(mobileNumber))
                .withType(program).build();
        ReflectionTestUtils.setField(subscription, "dateUtils", dateUtils);
        subscription.updateCycleInfo();
        return subscription;
    }

    private DateTime date(int year, int month, int date) {
        return new DateTime(year, month, date, 0, 0);
    }
}
