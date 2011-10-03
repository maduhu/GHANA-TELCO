package org.motechproject.ghana.mtn.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class SubscriptionMessageEventHandlerTest {

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
