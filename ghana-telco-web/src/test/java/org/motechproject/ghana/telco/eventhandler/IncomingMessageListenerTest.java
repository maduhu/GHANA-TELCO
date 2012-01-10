package org.motechproject.ghana.telco.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ghana.telco.controller.SubscriptionController;
import org.motechproject.ghana.telco.domain.dto.SubscriptionRequest;
import org.motechproject.model.MotechEvent;
import org.motechproject.sms.smpp.constants.EventKeys;
import org.motechproject.sms.smpp.constants.EventSubject;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.MockitoAnnotations.initMocks;

public class IncomingMessageListenerTest {
    private IncomingMessageListener incomingMessageListener;
    @Mock
    private SubscriptionController subscriptionController;

    @Before
    public void setUp() {
        initMocks(this);
        incomingMessageListener = new IncomingMessageListener(subscriptionController);
    }

    @Test
    public void shouldProcessIncomingMessages(){
        Map<String, Object> params = new HashMap<String, Object>();
        String sender = "0923842312";
        String message = "P 20";
        params.put(EventKeys.SENDER, sender);
        params.put(org.motechproject.sms.api.constants.EventKeys.MESSAGE, message);

        MotechEvent motechEvent = new MotechEvent(EventSubject.INBOUND_SMS, params);

        incomingMessageListener.processIncomingMessage(motechEvent);

        ArgumentCaptor<SubscriptionRequest> captor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        Mockito.verify(subscriptionController).handle(captor.capture());
        SubscriptionRequest request = captor.getValue();

        assertThat(request.getInputMessage(), is(message));
        assertThat(request.getSubscriberNumber(), is(sender));
    }

}
