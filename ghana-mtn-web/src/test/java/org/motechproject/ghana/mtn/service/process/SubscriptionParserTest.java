package org.motechproject.ghana.mtn.service.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.motechproject.ghana.mtn.service.InputMessageParser;
import org.motechproject.ghana.mtn.service.sms.SMSService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionParserTest {
    private SubscriptionParser parser;
    @Mock
    private SMSService smsService;
    @Mock
    private InputMessageParser inputParser;
    @Mock
    private MessageBundle messageBundle;

    @Before
    public void setUp() {
        initMocks(this);
        parser = new SubscriptionParser(inputParser, smsService, messageBundle);
    }

    @Test
    public void shouldSendSMSIfInputParserThrowsException() {
        String mobileNumber = "123";
        String errorMsg = "error";
        String input = "P 24";

        when(inputParser.parse(input)).thenThrow(new MessageParseFailException(""));
        when(messageBundle.get(MessageBundle.ENROLLMENT_FAILURE)).thenReturn(errorMsg);

        parser.parse(mobileNumber, input);

        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();
        assertEquals(mobileNumber,captured.getMobileNumber());
        assertEquals(errorMsg, captured.getMessage());
    }
}
