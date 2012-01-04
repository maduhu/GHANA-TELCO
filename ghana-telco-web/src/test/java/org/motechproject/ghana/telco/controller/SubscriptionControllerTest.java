package org.motechproject.ghana.telco.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.RegisterProgramSMS;
import org.motechproject.ghana.telco.domain.SMS;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.telco.process.UserMessageParserProcess;
import org.motechproject.ghana.telco.service.SMSHandler;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionControllerTest {
    private SubscriptionController controller;
    @Mock
    private UserMessageParserProcess parserHandle;
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
