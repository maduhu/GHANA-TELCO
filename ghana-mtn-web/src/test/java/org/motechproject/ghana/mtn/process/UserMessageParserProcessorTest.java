package org.motechproject.ghana.mtn.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.RegisterProgramSMS;
import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.motechproject.ghana.mtn.parser.RelativeProgramMessageParser;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.parser.CompositeInputMessageParser;
import org.motechproject.ghana.mtn.vo.ParsedRequest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserMessageParserProcessorTest {
    private UserMessageParserProcess parserHandle;
    @Mock
    private SMSService smsService;
    @Mock
    private CompositeInputMessageParser inputParser;
    @Mock
    private MessageBundle messageBundle;
    @Mock
    private RelativeProgramMessageParser relativeProgramMessageHandler;

    @Before
    public void setUp() {
        initMocks(this);
        parserHandle = new UserMessageParserProcess(inputParser, relativeProgramMessageHandler, smsService, messageBundle);
    }

    @Test
    public void shouldSendSMSIfInputParserThrowsException() {
        String mobileNumber = "123";
        String errorMsg = "error";
        String input = "P 24";

        when(inputParser.parse(input, mobileNumber)).thenThrow(new MessageParseFailException(""));
        when(messageBundle.get(MessageBundle.ENROLLMENT_FAILURE)).thenReturn(errorMsg);

        parserHandle.process(mobileNumber, input);

        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();
        assertEquals(mobileNumber,captured.getMobileNumber());
        assertEquals(errorMsg, captured.getMessage());
    }

    @Test
    public void shouldParseUsingTheInputSentInRelativeServiceAndAddReferrer() {
        String senderMobileNumber = "9500012345";
        String subscriberNumber = "0987654321";
        String inputMessage = "P 25";
        String input = "R " + subscriberNumber + " " + inputMessage;

        when(relativeProgramMessageHandler.parse(input, senderMobileNumber)).thenReturn(new ParsedRequest(subscriberNumber, senderMobileNumber, inputMessage));
        SMS sms = new RegisterProgramSMS(inputMessage, new SubscriptionBuilder().build()).setFromMobileNumber(subscriberNumber).setReferrer(senderMobileNumber);
        when(inputParser.parse(inputMessage, subscriberNumber)).thenReturn(sms);

        SMS createdSMS = parserHandle.process(senderMobileNumber, input);

        assertThat(createdSMS.getReferrer(), is(senderMobileNumber));
        assertThat(createdSMS.getFromMobileNumber(), is(subscriberNumber));
        verify(inputParser).parse(inputMessage, subscriberNumber);
    }

}
