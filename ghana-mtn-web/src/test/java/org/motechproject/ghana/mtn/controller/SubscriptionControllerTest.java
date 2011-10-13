package org.motechproject.ghana.mtn.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.RegisterProgramSMS;
import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.process.SubscriptionUserMessageParser;
import org.motechproject.ghana.mtn.service.SMSHandler;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionControllerTest {
    private SubscriptionController controller;
    @Mock
    private SubscriptionUserMessageParser parserHandle;
    @Mock
    private SMSHandler handler;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new SubscriptionController(parserHandle, handler);
    }

    @Test
    public void ShouldParseAndValidateInputMessage() throws IOException {
        String subscriberNumber = "1234567890";
        String inputMessage = "C 25";

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setSubscriberNumber(subscriberNumber);
        subscriptionRequest.setInputMessage(inputMessage);

        Subscription subscription = mock(Subscription.class);
        SMS sms = spy(new RegisterProgramSMS(inputMessage, subscription).setFromMobileNumber(subscriberNumber));
        when(parserHandle.process(subscriberNumber, inputMessage)).thenReturn(sms);

        controller.handle(subscriptionRequest);
        
        verify(sms).process(handler);
    }

}
