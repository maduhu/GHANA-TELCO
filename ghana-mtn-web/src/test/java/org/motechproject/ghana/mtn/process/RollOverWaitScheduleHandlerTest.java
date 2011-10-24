package org.motechproject.ghana.mtn.process;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RollOverWaitScheduleHandlerTest {
    private RollOverWaitSchedule rollOverWaitScheduleHandler;
    @Mock
    private MotechSchedulerService scheduledService;

    @Before
    public void setUp() {
        initMocks(this);
        rollOverWaitScheduleHandler = new RollOverWaitSchedule(scheduledService);
    }

    @Test
    public void shouldCreateRunOnceSchedule() {
        Subscription subscription = mock(Subscription.class);

        rollOverWaitScheduleHandler.startScheduleWaitFor(subscription);

        verify(subscription).subscriberNumber();

        ArgumentCaptor<RunOnceSchedulableJob> captor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(scheduledService).scheduleRunOnceJob(captor.capture());
        RunOnceSchedulableJob schedulableJob = captor.getValue();

        assertThat(new DateTime().hourOfDay().addToCopy(3).toLocalDate(), is(new DateTime(schedulableJob.getStartDate()).toLocalDate()));
    }
}
