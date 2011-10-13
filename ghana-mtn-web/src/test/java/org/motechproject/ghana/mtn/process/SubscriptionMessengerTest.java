package org.motechproject.ghana.mtn.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.repository.AllProgramMessages;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class SubscriptionMessengerTest {
    private SubscriptionMessenger messenger;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;
    @Mock
    private AllProgramMessages allProgramMessages;
    @Mock
    private AllSubscriptions allSubscriptions;

    @Before
    public void setUp() {
        initMocks(this);
        messenger = new SubscriptionMessenger(smsService, messageBundle, allProgramMessages, allSubscriptions);
    }

    @Test
    public void shouldSMSRightMessageAndUpdateSubscriptionState() {
        Subscription subscription = mock(Subscription.class);
        ProgramType programType = mock(ProgramType.class);
        ProgramMessage programMessage = mock(ProgramMessage.class);
        Week currentWeek = new Week();
        Day currentDay = Day.FRIDAY;
        String mobileNumber = "123";
        String content = "content";

        when(subscription.getProgramType()).thenReturn(programType);
        when(subscription.currentWeek()).thenReturn(currentWeek);
        when(subscription.currentDay()).thenReturn(currentDay);
        when(subscription.subscriberNumber()).thenReturn(mobileNumber);
        when(subscription.alreadySent(programMessage)).thenReturn(false);
        when(programMessage.getContent()).thenReturn(content);
        when(allProgramMessages.findBy(programType, currentWeek, currentDay)).thenReturn(programMessage);
        
        messenger.process(subscription);
        
        verify(subscription).updateLastMessageSent();

        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();
        assertEquals(mobileNumber,captured.getMobileNumber());
        assertEquals(content,captured.getMessage());
    }
}
