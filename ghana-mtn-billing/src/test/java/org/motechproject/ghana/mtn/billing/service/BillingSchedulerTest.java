package org.motechproject.ghana.mtn.billing.service;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.DefaultedBillingRequest;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.util.DateUtil;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.*;
import static org.motechproject.scheduler.MotechSchedulerService.JOB_ID_KEY;
import static org.motechproject.valueobjects.WallTimeUnit.Day;
import static org.motechproject.valueobjects.WallTimeUnit.Week;

public class BillingSchedulerTest {

    public static final String CRON = "0 0 5 %s *";
    private BillingScheduler billingScheduler;
    @Mock
    private MotechSchedulerService schedulerService;

    @Before
    public void setUp() {
        initMocks(this);
        billingScheduler = new BillingScheduler(schedulerService, CRON);
    }

    @Test
    public void shouldRaiseACronJobWithPlatformSchedulerService() {
        BillingCycleRequest request = mock(BillingCycleRequest.class);
        DateTime cycleStartDate = DateUtil.now();
        Date startDate = cycleStartDate.monthOfYear().addToCopy(1).toDate();
        DateTime endDateTime = cycleStartDate.weekyear().addToCopy(35);

        when(request.getMobileNumber()).thenReturn("123");
        when(request.programKey()).thenReturn("program");
        when(request.getCycleStartDate()).thenReturn(cycleStartDate);
        when(request.getCycleEndDate()).thenReturn(endDateTime);

        billingScheduler.startFor(request);

        ArgumentCaptor<CronSchedulableJob> captor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService).scheduleJob(captor.capture());

        CronSchedulableJob job = captor.getValue();
        MotechEvent motechEvent = job.getMotechEvent();
        Map<String, Object> params = motechEvent.getParameters();

        assertEquals(MONTHLY_BILLING_SCHEDULE_SUBJECT, motechEvent.getSubject());
        assertEquals("program.123", params.get(JOB_ID_KEY));
        assertEquals("123", params.get(BillingScheduler.EXTERNAL_ID_KEY));
        assertEquals("program", params.get(BillingScheduler.PROGRAM_KEY));
        assertEquals(format(CRON, cycleStartDate.getDayOfMonth()), job.getCronExpression());
        assertEquals(startDate, job.getStartTime());
        assertEquals(endDateTime.toDate(), job.getEndTime());
    }

    @Test
    public void shouldStopScheduledJobs() {
        BillingCycleRequest request = mock(BillingCycleRequest.class);
        when(request.getMobileNumber()).thenReturn("123");
        when(request.programKey()).thenReturn("program");

        billingScheduler.stopFor(request);

        verify(schedulerService).unscheduleJob(MONTHLY_BILLING_SCHEDULE_SUBJECT, "program.123");
    }

    @Test
    public void shouldCheckIfTheCronWorksWithTheBillingDate() throws ParseException {
        final String expression = "0 0 5 %s * ?";
        String every7th = format(expression, 7);
        final CronExpression cronExpression = new CronExpression(every7th);

        assertEquals(date(2011, 7, 7, 5, 0), cronExpression.getNextValidTimeAfter(date(2011, 7, 6, 0, 0)));
        assertEquals(date(2011, 8, 7, 5, 0), cronExpression.getNextValidTimeAfter(date(2011, 7, 7, 5, 0)));
        assertEquals(date(2011, 8, 7, 5, 0), cronExpression.getNextValidTimeAfter(date(2011, 7, 7, 10, 0)));
        assertEquals(date(2011, 10, 7, 5, 0), cronExpression.getNextValidTimeAfter(date(2011, 9, 9, 10, 0)));

    }

    @Test
    public void shouldStartDefaultedDailyBillingSchedule() {
        IProgramType programType = mock(IProgramType.class);
        DateTime now = DateTime.now();
        DateTime cycleEndDate = now.dayOfMonth().addToCopy(1);
        String mobileNumber = "123456890";
        DefaultedBillingRequest request = new DefaultedBillingRequest(mobileNumber, programType, now, Day, cycleEndDate);

        when(programType.getProgramKey()).thenReturn("programKey");
        billingScheduler.startDefaultedBillingSchedule(request);

        ArgumentCaptor<RepeatingSchedulableJob> captor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).scheduleRepeatingJob(captor.capture());

        RepeatingSchedulableJob scheduledJob = captor.getValue();
        Map<String, Object> parameters = scheduledJob.getMotechEvent().getParameters();

        assertThat(scheduledJob.getStartTime(), is(now.toDate()));
        assertThat(scheduledJob.getEndTime(), is(cycleEndDate.toDate()));
        assertThat(scheduledJob.getMotechEvent().getSubject(), is(DEFAULTED_DAILY_SCHEDULE));
        assertThat(scheduledJob.getRepeatInterval(), is(getRepeatingInterval(1)));

        assertThat((String) parameters.get(EXTERNAL_ID_KEY), is(mobileNumber));
        assertThat((String) parameters.get(PROGRAM_KEY), is(programType.getProgramKey()));
        assertThat((String) parameters.get(JOB_ID_KEY), is(programType.getProgramKey() + "." + mobileNumber));
    }

    private Long getRepeatingInterval(int days) {
        return valueOf(Days.days(days).toPeriod().toStandardSeconds().getSeconds() * 1000);
    }

    @Test
    public void shouldStartDefaultedWeeklyBillingScheduleForWallTimeUnitAsWeek() {

        IProgramType programType = mock(IProgramType.class);
        DateTime now = DateTime.now();
        DateTime cycleEndDate = now.dayOfMonth().addToCopy(1);
        String mobileNumber = "123456890";
        DefaultedBillingRequest request = new DefaultedBillingRequest(mobileNumber, programType, now, Week, cycleEndDate);

        when(programType.getProgramKey()).thenReturn("programKey");
        billingScheduler.startDefaultedBillingSchedule(request);

        ArgumentCaptor<RepeatingSchedulableJob> captor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(schedulerService).scheduleRepeatingJob(captor.capture());

        RepeatingSchedulableJob scheduledJob = captor.getValue();
        Map<String, Object> parameters = scheduledJob.getMotechEvent().getParameters();

        assertThat(scheduledJob.getStartTime(), is(now.toDate()));
        assertThat(scheduledJob.getEndTime(), is(cycleEndDate.toDate()));
        assertThat(scheduledJob.getMotechEvent().getSubject(), is(DEFAULTED_WEEKLY_SCHEDULE));
        assertThat(scheduledJob.getRepeatInterval(), is(getRepeatingInterval(7)));
        assertThat((String) parameters.get(EXTERNAL_ID_KEY), is(mobileNumber));
        assertThat((String) parameters.get(PROGRAM_KEY), is(programType.getProgramKey()));
        assertThat((String) parameters.get(JOB_ID_KEY), is(programType.getProgramKey() + "." + mobileNumber));
    }
    
    @Test
    public void shouldStopDefaultedBillingSchedule() {

        IProgramType programType = programType("programkey");
        String subscriberNumber = "9500012345";
        billingScheduler.stop(new DefaultedBillingRequest(subscriberNumber, programType, Day));
        verify(schedulerService).unscheduleJob(DEFAULTED_DAILY_SCHEDULE, programType.getProgramKey() +"." +  subscriberNumber);

        reset(schedulerService);

        billingScheduler.stop(new DefaultedBillingRequest(subscriberNumber, programType, Week));
        verify(schedulerService).unscheduleJob(DEFAULTED_WEEKLY_SCHEDULE, programType.getProgramKey() +"." +  subscriberNumber);
    }

    private IProgramType programType(String programkey) {
        IProgramType programType = mock(IProgramType.class);
        when(programType.getProgramKey()).thenReturn(programkey);
        return programType;
    }

    private Date date(int year, int month, int day, int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day, hour, min, 0);
        return calendar.getTime();
    }
}
