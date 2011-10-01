package org.motechproject.ghana.mtn.service.listener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.SpringTestContext;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

public class PregnancyMessageListenerTest extends SpringTestContext {

    PregnancyMessageListener pregnancyMessageListener;
    @Mock
    SubscriptionService subscriptionService;

    @Before
    public void setUp() {
        pregnancyMessageListener = spy(new PregnancyMessageListener());
        ReflectionTestUtils.setField(pregnancyMessageListener, "subscriptionService", subscriptionService);                
    }

    @Test
    public void shouldSendReminderNotificationBasedOnCurrentWeekOfCustomer() {

        String subscriberNumber = "9812398123", programName = "Pregnancy";
        Subscription subscription = mock(Subscription.class);
        when(subscriptionService.findBy(subscriberNumber, programName)).thenReturn(subscription);
        pregnancyMessageListener.handleWeeklyReminder(motechEvent(subscriberNumber, programName));

        verify(subscriptionService).findBy(subscriberNumber, programName);
        verify(pregnancyMessageListener).sendReminder(subscription);
        
    }

    private MotechEvent motechEvent(String subscriberNumber, String programName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(EventKeys.CAMPAIGN_NAME_KEY, programName);
        map.put(EventKeys.EXTERNAL_ID_KEY, subscriberNumber);
        return new MotechEvent(MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, map);
    }
}
