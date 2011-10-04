package org.motechproject.ghana.mtn.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.repository.AllProgramMessages;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionMessageEventHandlerTest {

    private ProgramMessageEventHandler subscriptionMessageHandler;
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private AllProgramMessages allSubscriptionMessages;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionMessageHandler = new ProgramMessageEventHandler(allSubscriptions, allSubscriptionMessages);
    }

    @Test
    public void shouldSendPickRightReminderAndSendIfNotAlreadySent() {
        String subscriberNumber = "externalId";
        String programName = "pregnancy";

        Map params = new HashMap();
        params.put(EventKeys.CAMPAIGN_NAME_KEY, programName);
        params.put(EventKeys.EXTERNAL_ID_KEY, subscriberNumber);
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, params);

        Week week = new Week(21);
        Day day = Day.FRIDAY;
        ProgramType programType = new ProgramType();
        Subscription subscription = mock(Subscription.class);
        ProgramMessage subscriptionMessage = mock(ProgramMessage.class);

        when(subscription.currentDay()).thenReturn(day);
        when(subscription.currentWeek()).thenReturn(week);
        when(subscription.getProgramType()).thenReturn(programType);
        when(subscription.alreadySent(subscriptionMessage)).thenReturn(false);
        when(allSubscriptions.findBy(subscriberNumber, programName)).thenReturn(subscription);
        when(allSubscriptionMessages.findBy(programType, week, day)).thenReturn(subscriptionMessage);

        subscriptionMessageHandler.sendMessageReminder(motechEvent);
        verify(subscription).updateLastMessageSent();
        verify(allSubscriptions).update(subscription);
    }
}
