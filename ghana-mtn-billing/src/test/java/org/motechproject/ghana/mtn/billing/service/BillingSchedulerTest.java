package org.motechproject.ghana.mtn.billing.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;

import java.util.Date;
import java.util.Map;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillingSchedulerTest {

    private BillingScheduler billingScheduler;
    @Mock
    private MotechSchedulerService schedulerService;

    @Before
    public void setUp() {
        initMocks(this);
        billingScheduler = new BillingScheduler(schedulerService, "jobKey", "0 0 5 %s *");
    }

    @Test
    public void shouldRaiseACronJobWithPlatformSchedulerService() {
        BillingCycleRequest request = mock(BillingCycleRequest.class);
        DateTime cycleStartDate = DateTime.now();
        Date startDate = cycleStartDate.monthOfYear().addToCopy(1).toDate();

        when(request.getMobileNumber()).thenReturn("123");
        when(request.programName()).thenReturn("program");
        when(request.getCycleStartDate()).thenReturn(cycleStartDate);

        billingScheduler.startFor(request);

        ArgumentCaptor<CronSchedulableJob> captor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService).scheduleJob(captor.capture());

        CronSchedulableJob job = captor.getValue();
        MotechEvent motechEvent = job.getMotechEvent();
        Map<String, Object> params = motechEvent.getParameters();

        assertEquals("jobKey",motechEvent.getSubject());
        assertEquals("jobKey.program.123",params.get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals("123",params.get(SchedulerParamsBuilder.EXTERNAL_ID_KEY));
        assertEquals("program", params.get(SchedulerParamsBuilder.PROGRAM));
        assertEquals(format("0 0 5 %s *", cycleStartDate.getDayOfMonth()), job.getCronExpression());
        assertEquals(startDate, job.getStartTime());
    }


}
