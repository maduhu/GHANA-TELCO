package org.motechproject.ghana.mtn.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.SpringTestContext;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionMessage;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

public class SubscriptionMessageEventHandlerTest extends SpringTestContext {

    SubscriptionMessageEventHandler subscriptionMessageHandler;
    @Mock
    SubscriptionService subscriptionService;

    @Before
    public void setUp() {
        subscriptionMessageHandler = spy(new SubscriptionMessageEventHandler());
        ReflectionTestUtils.setField(subscriptionMessageHandler, "subscriptionService", subscriptionService);
    }

    @Test
    public void should() {
        assertTrue(true);
    }

}
