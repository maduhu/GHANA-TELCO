package org.motechproject.ghana.telco.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.MessageBundle;
import org.motechproject.ghana.telco.domain.ProgramMessage;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.telco.domain.vo.Week;
import org.motechproject.ghana.telco.repository.AllProgramMessages;
import org.motechproject.ghana.telco.repository.AllSubscriptions;
import org.motechproject.ghana.telco.service.SMSService;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessengerProcessorTest {
    private MessengerProcess messenger;
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
        messenger = new MessengerProcess(smsService, messageBundle, allProgramMessages, allSubscriptions);
    }

    @Test
    public void shouldSMSRightMessageAndUpdateSubscriptionState() {
        String messageKey = "key";
        Subscription subscription = mock(Subscription.class);
        ProgramType programType = mock(ProgramType.class);
        ProgramMessage programMessage = mock(ProgramMessage.class);
        Week currentWeek = new Week();
        DayOfWeek currentDay = DayOfWeek.Friday;
        String mobileNumber = "123";
        String content = "content";
        Time deliveryTime = new Time(10, 30);
        when(subscription.getProgramType()).thenReturn(programType);
        when(subscription.currentDay()).thenReturn(currentDay);
        when(subscription.subscriberNumber()).thenReturn(mobileNumber);
        when(programMessage.getContent()).thenReturn(content);
        when(allProgramMessages.findBy(messageKey)).thenReturn(programMessage);

        messenger.process(subscription, messageKey);

        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();
        assertEquals(mobileNumber, captured.getMobileNumber());
        assertEquals(content, captured.getMessage());
    }
}

