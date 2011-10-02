package org.motechproject.ghana.mtn.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.SpringTestContext;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionMessage;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.repository.AllSubscriptionMessages;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

public class SubscriptionMessageEventHandlerTest extends SpringTestContext {

    private SubscriptionMessageEventHandler subscriptionMessageHandler;
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private AllSubscriptionMessages allSubscriptionMessages;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionMessageHandler = new SubscriptionMessageEventHandler(allSubscriptions, allSubscriptionMessages);
    }

    @Test
    public void shouldSendPickRightReminderAndSendIfNotAlreadySent() {
        String subscriberNo = "externalId";
        String programName = "pregnancy";

        Map params = new HashMap();
        params.put(EventKeys.CAMPAIGN_NAME_KEY, programName);
        params.put(EventKeys.EXTERNAL_ID_KEY, subscriberNo);
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, params);

        Week week = new Week(21);
        Day day = Day.FRIDAY;
        SubscriptionType subscriptionType = new SubscriptionType();
        Subscription subscription = mock(Subscription.class);
        SubscriptionMessage subscriptionMessage = mock(SubscriptionMessage.class);

        when(subscription.currentDay()).thenReturn(day);
        when(subscription.currentWeek()).thenReturn(week);
        when(subscription.getSubscriptionType()).thenReturn(subscriptionType);
        when(subscription.alreadySent(subscriptionMessage)).thenReturn(false);
        when(allSubscriptions.findBy(subscriberNo, programName)).thenReturn(subscription);
        when(allSubscriptionMessages.findBy(subscriptionType, week, day)).thenReturn(subscriptionMessage);

        subscriptionMessageHandler.sendMessageReminder(motechEvent);
        verify(subscription).updateLastMessageSent();
        verify(allSubscriptions).update(subscription);
    }

}
