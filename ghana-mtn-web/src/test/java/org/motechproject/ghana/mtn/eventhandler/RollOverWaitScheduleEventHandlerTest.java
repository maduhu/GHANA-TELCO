package org.motechproject.ghana.mtn.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.service.SchedulerParamsBuilder;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.motechproject.model.MotechEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.process.RollOverWaitSchedule.ROLLOVER_WAIT_SCHEDULE;

public class RollOverWaitScheduleEventHandlerTest {
    private RollOverWaitScheduleEventHandler rollOverWaitScheduleEventHandler;
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void setUp() {
        initMocks(this);
        rollOverWaitScheduleEventHandler = new RollOverWaitScheduleEventHandler(subscriptionService);
    }


    @Test
    public void shouldRollOverOrRetainSubscription() {
        String subscriberNumber = "1234567890";
        MotechEvent event = new MotechEvent(ROLLOVER_WAIT_SCHEDULE, new SchedulerParamsBuilder().withExternalId(subscriberNumber).params());

        rollOverWaitScheduleEventHandler.rollOverSchedule(event);

        verify(subscriptionService).retainOrRollOver(subscriberNumber, true);
    }
}