package org.motechproject.ghana.telco.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.process.MessengerProcess;
import org.motechproject.ghana.telco.service.SubscriptionService;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProgramMessageEventHandlerTest {

    private ProgramMessageEventHandler programMessageEventHandler;
    @Mock
    private MessengerProcess messenger;
    @Mock
    private SubscriptionService service;
    @Mock
    private AllMessageCampaigns allMessageCampaigns;

    @Before
    public void setUp() {
        initMocks(this);
        programMessageEventHandler = new ProgramMessageEventHandler(messenger, service, allMessageCampaigns);
    }

    @Test
    public void shouldUseSubscriptionMessageSenderAfterPickingRightSubscription() {
        String subscriberNumber = "externalId";
        String programKey = ProgramType.PREGNANCY;
        Subscription subscription = mock(Subscription.class);
        RepeatingCampaignMessage repeatingCampaignMessage = mock(RepeatingCampaignMessage.class);
        Time deliveryTime = new Time(10, 30);

        Map params = new HashMap();
        params.put(EventKeys.CAMPAIGN_NAME_KEY, programKey);
        params.put(EventKeys.EXTERNAL_ID_KEY, subscriberNumber);
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, params);
        motechEvent.setLastEvent(true);

        when(service.findActiveSubscriptionFor(subscriberNumber, programKey)).thenReturn(subscription);
        when(subscription.programKey()).thenReturn(programKey);
        when(allMessageCampaigns.getCampaignMessageByMessageName(anyString(), anyString())).thenReturn(repeatingCampaignMessage);
        when(repeatingCampaignMessage.deliverTime()).thenReturn(deliveryTime);

        programMessageEventHandler.sendMessageReminder(motechEvent);

        verify(messenger).process(subscription, (String) params.get(EventKeys.MESSAGE_KEY), deliveryTime);
        verify(service).rollOverByEvent(subscription);
    }
}
