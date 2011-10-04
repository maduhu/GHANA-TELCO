package org.motechproject.ghana.mtn.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ghana.mtn.domain.MessageAudit;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.repository.AllMessageAudits;
import org.motechproject.ghana.mtn.repository.AllProgramMessages;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProgramMessageEventHandlerTest {

    private ProgramMessageEventHandler programMessageEventHandler;
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private AllProgramMessages allSubscriptionMessages;
    @Mock
    private AllMessageAudits allMessageAudits;

    @Before
    public void setUp() {
        initMocks(this);
        programMessageEventHandler = new ProgramMessageEventHandler(allSubscriptions, allSubscriptionMessages, allMessageAudits);
    }

    @Test
    public void shouldSendPickRightReminderAndSendIfNotAlreadySent() {
        String subscriberNumber = "externalId";
        String programName = "pregnancy";
        String messageContent = "sample message content";

        Map params = new HashMap();
        params.put(EventKeys.CAMPAIGN_NAME_KEY, programName);
        params.put(EventKeys.EXTERNAL_ID_KEY, subscriberNumber);
        MotechEvent motechEvent = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, params);

        Week week = new Week(21);
        Day day = Day.FRIDAY;
        ProgramType programType = new ProgramType();
        Subscription subscription = mock(Subscription.class);
        ProgramMessage programMessage = mock(ProgramMessage.class);

        when(subscription.currentDay()).thenReturn(day);
        when(subscription.currentWeek()).thenReturn(week);
        when(subscription.getProgramType()).thenReturn(programType);
        when(subscription.alreadySent(programMessage)).thenReturn(false);
        when(programMessage.getContent()).thenReturn(messageContent);

        when(allSubscriptions.findBy(subscriberNumber, programName)).thenReturn(subscription);
        when(allSubscriptionMessages.findBy(programType, week, day)).thenReturn(programMessage);

        programMessageEventHandler.sendMessageReminder(motechEvent);
        verify(subscription).updateLastMessageSent();
        verify(allSubscriptions).update(subscription);

        ArgumentCaptor<MessageAudit> auditCapture = ArgumentCaptor.forClass(MessageAudit.class);
        verify(allMessageAudits).add(auditCapture.capture());
        MessageAudit capturedAudit = auditCapture.getValue();
        assertEquals(programName, capturedAudit.getProgramName());
        assertEquals(messageContent, capturedAudit.getContent());
    }
}
