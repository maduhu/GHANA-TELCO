package org.motechproject.ghana.mtn.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.process.SubscriptionParser;
import org.motechproject.ghana.mtn.service.SubscriptionService;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionControllerTest {
    private SubscriptionController controller;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionParser parser;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new SubscriptionController(subscriptionService, parser);
    }

    @Test
    public void ShouldParseAndValidateInputMessage() throws IOException {
        String subscriberNumber = "1234567890";
        String inputMessage = "C 25";

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setSubscriberNumber(subscriberNumber);
        subscriptionRequest.setInputMessage(inputMessage);

        Subscription subscription = mock(Subscription.class);
        when(parser.parse(subscriberNumber, inputMessage)).thenReturn(subscription);

        controller.handle(subscriptionRequest);

        verify(subscriptionService).start(subscription);
        ArgumentCaptor<Subscriber> captor = ArgumentCaptor.forClass(Subscriber.class);
        verify(subscription).setSubscriber(captor.capture());
        Subscriber captured = captor.getValue();
        assertEquals(subscriberNumber, captured.getNumber());
    }

}
