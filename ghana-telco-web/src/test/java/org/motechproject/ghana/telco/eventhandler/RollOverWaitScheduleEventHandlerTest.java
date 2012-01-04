package org.motechproject.ghana.telco.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.telco.service.SubscriptionService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.telco.process.RollOverWaitSchedule.ROLLOVER_WAIT_SCHEDULE;

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
        Map params=new HashMap<String,Object>();
        params.put(EventKeys.EXTERNAL_ID_KEY,subscriberNumber);
        MotechEvent event = new MotechEvent(ROLLOVER_WAIT_SCHEDULE, params);

        rollOverWaitScheduleEventHandler.rollOverSchedule(event);

        verify(subscriptionService).retainOrRollOver(subscriberNumber, true);
    }
}