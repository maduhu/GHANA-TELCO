package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.process.UserMessageParserHandle;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSHandlerTest {

    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private UserMessageParserHandle parserHandle;
    private SMSHandler handler;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new SMSHandler(subscriptionService);
    }

    @Test
    public void ShouldRegisterSubscriberForProgram() throws IOException {
        String subscriberNumber = "1234567890";
        String inputMessage = "C 25";

        Subscription subscription = new SubscriptionBuilder().build();
        SMS.RegisterProgramSMS sms = (SMS.RegisterProgramSMS) new SMS.RegisterProgramSMS(inputMessage, subscription).setFromMobileNumber(subscriberNumber);

        handler.register(sms);

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionService).start(captor.capture());
        Subscriber subscriber = captor.getValue().getSubscriber();
        assertEquals(subscriberNumber, subscriber.getNumber());
    }
}
