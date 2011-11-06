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
    public void shouldReturnCurrentRunningWeekAsNull_IfCycleStartDateFallsOnFuture() {

        DateTime satOct8 = date(2011, 10, 8);
        Subscription registeredOn_satOct8 = subscription("9999933333", satOct8, new Week(10), programType("Pregnancy"));

        mockCurrentDate(date(2011, 2, 5)); // sat
        assertEquals(date(2011, 10, 10).toLocalDate(), registeredOn_satOct8.getCycleStartDate().toLocalDate());
        assertNull(registeredOn_satOct8.currentWeek());
    }

    @Test
    public void shouldReturnCurrentRunningWeekAsRegisteredWeek_IfRegisteredOnFridayOrAfter() {

        DateTime friOct14 = date(2011, 10, 14);
        Subscription registeredOn_satOct14 = subscription("9999933333", friOct14, new Week(10), programType("Pregnancy"));

        mockCurrentDate(date(2011, 10, 15)); // sat
        assertEquals(date(2011, 10, 17).toLocalDate(), registeredOn_satOct14.getCycleStartDate().toLocalDate());
        assertNull(registeredOn_satOct14.currentWeek());

        mockCurrentDate(date(2011, 10, 17)); // mon
        assertWeek(new Week(10), registeredOn_satOct14.currentWeek());
    }

    @Test
    public void shouldReturnCurrentRunningWeekForSubscriptionProgramBased_OnCycleStartDateAndSundayAsStartOfWeek() {
        DateTime monJan31 = date(2011, 1, 31);
        DateTime wedFeb2 = date(2011, 2, 2);
        DateTime satFeb5 = date(2011, 2, 5);
        DateTime sunFeb6 = date(2011, 2, 6);
        DateTime wedFeb24 = date(2011, 2, 24);

        Subscription registeredOn_monJan31 = subscription("9999933333", monJan31, new Week(10), programType("Pregnancy"));
        Subscription registeredOn_wedFeb2 = subscription("9999933333", wedFeb2, new Week(6), programType("Pregnancy"));
        Subscription registeredOn_satFeb5 = subscription("9999933333", satFeb5, new Week(6), programType("Child"));
        Subscription registeredOn_sunFeb6 = subscription("9999933333", sunFeb6, new Week(8), programType("Child"));
        Subscription registeredOn_wedFeb24 = subscription("9999933333", wedFeb24, new Week(9), programType("Child"));

        mockCurrentDate(date(2011, 2, 5)); // sat
        assertWeek(new Week(10), registeredOn_monJan31.currentWeek());
        assertWeek(new Week(6), registeredOn_wedFeb2.currentWeek());
        assertNull(registeredOn_satFeb5.currentWeek());
        assertNull(registeredOn_wedFeb24.currentWeek());

        mockCurrentDate(date(2011, 2, 6)); // sun
        Week weekIsNullSinceCycleStartDateIsInFuture = null;
        assertWeek(new Week(11), registeredOn_monJan31.currentWeek());
        assertWeek(new Week(7), registeredOn_wedFeb2.currentWeek());
        assertWeek(weekIsNullSinceCycleStartDateIsInFuture, registeredOn_satFeb5.currentWeek());
        assertWeek(weekIsNullSinceCycleStartDateIsInFuture, registeredOn_sunFeb6.currentWeek());
        assertWeek(weekIsNullSinceCycleStartDateIsInFuture, registeredOn_wedFeb24.currentWeek());

        mockCurrentDate(date(2011, 2, 19)); // sat
        assertWeek(new Week(12), registeredOn_monJan31.currentWeek());
        assertWeek(new Week(8), registeredOn_wedFeb2.currentWeek());
        assertWeek(new Week(7), registeredOn_satFeb5.currentWeek());
        assertWeek(new Week(9), registeredOn_sunFeb6.currentWeek());
        assertNull(registeredOn_wedFeb24.currentWeek());

        mockCurrentDate(date(2011, 2, 27)); // sun
        assertWeek(new Week(14), registeredOn_monJan31.currentWeek());
        assertWeek(new Week(10), registeredOn_wedFeb2.currentWeek());
        assertWeek(new Week(9), registeredOn_satFeb5.currentWeek());
        assertWeek(new Week(11), registeredOn_sunFeb6.currentWeek());
        assertWeek(new Week(10), registeredOn_wedFeb24.currentWeek());
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
    public void shouldReturnStartBillingDateAsFirstOfNextMonthIfBillingCycleDateEndsOn29or30or31OfMonth() {
        DateTime feb28Mon = date(2011, 2, 28);
        DateTime sep30Fri = date(2011, 9, 30);
        DateTime oct1Sat_CycleDateWillBe_oct3Mon = date(2011, 10, 1);
        DateTime oct29Sat = date(2011, 10, 29);
        DateTime dec31Sat_CycleDateWillBe_jan2Mon = date(2011, 12, 31);

        String mobileNumber = "9999933333";
        ProgramType pregnancyProgram = programType("Pregnancy");
        Subscription registeredOn_feb28Mon = subscription(mobileNumber, feb28Mon, new Week(10), pregnancyProgram);
        Subscription registeredOn_sep30Fri = subscription(mobileNumber, sep30Fri, new Week(6), pregnancyProgram);
        Subscription registeredOn_oct1Sat_CycleDateWillBe_oct3Mon = subscription(mobileNumber, oct1Sat_CycleDateWillBe_oct3Mon, new Week(6), programType("Child"));
        Subscription registeredOn_oct29Sat = subscription(mobileNumber, oct29Sat, new Week(8), programType("Child"));
        Subscription registeredOn_dec31Sat_CycleDateWillBe_jan2Mon = subscription(mobileNumber, dec31Sat_CycleDateWillBe_jan2Mon, new Week(9), programType("Child"));

        assertEquals(date(2011, 3, 2), registeredOn_feb28Mon.updateCycleInfo().getBillingStartDate());
        assertEquals(date(2011, 10, 3), registeredOn_sep30Fri.updateCycleInfo().getBillingStartDate());
        assertEquals(date(2011, 10, 3), registeredOn_oct1Sat_CycleDateWillBe_oct3Mon.updateCycleInfo().getBillingStartDate());
        assertEquals(date(2011, 11, 1), registeredOn_oct29Sat.updateCycleInfo().getBillingStartDate());
        assertEquals(date(2012, 1, 2), registeredOn_dec31Sat_CycleDateWillBe_jan2Mon.updateCycleInfo().getBillingStartDate());
    }

    @Test
    public void shouldReturnCurrentDay() {
        Subscription sub1 = subscription("9999933333", new DateTime(2012, 2, 2, 10, 0), new Week(6), programType("Pregnancy"));
        mockCurrentDate(new DateTime(2012, 1, 1, 1, 1));
        assertEquals(Day.SUNDAY, sub1.currentDay());

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
        Subscription subscription = new SubscriptionBuilder().withRegistrationDate(registeredDate).withStartWeekAndDay(new WeekAndDay(startWeek, Day.MONDAY))
                .withStatus(SubscriptionStatus.ACTIVE).withSubscriber(new Subscriber(mobileNumber))
                .withType(program).build();
        ReflectionTestUtils.setField(subscription, "dateUtils", dateUtils);
        subscription.updateCycleInfo();
        return subscription;
    }

    @Test
    public void shouldCheckIfMessageAlreadySent() {
        Subscription sub1 = new Subscription();
        assertFalse(sub1.alreadySent(new ProgramMessage()));
    }

    private DateTime date(int year, int month, int date) {
        return new DateTime(year, month, date, 0, 0);
    }
}
