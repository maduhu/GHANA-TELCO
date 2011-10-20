package org.motechproject.ghana.mtn.parser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.domain.ShortCode;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.motechproject.ghana.mtn.repository.AllShortCodes;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class StopMessageParserTest {

    StopMessageParser parser;
    @Mock
    private AllProgramTypes allProgramTypes;
    @Mock
    private AllShortCodes allShortCodes;

    ProgramType pregnancy;
    ProgramType childCare;
    private String senderMobileNumber = "123456";

    @Before
    public void setUp() {
        initMocks(this);

        pregnancy = new ProgramTypeBuilder().withShortCode("p").withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build();
        childCare = new ProgramTypeBuilder().withShortCode("c").withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build();
        when(allProgramTypes.getAll()).thenReturn(asList(pregnancy, childCare));
        parser = new StopMessageParser();
        when(allShortCodes.getAllCodesFor(ShortCode.STOP))
                .thenReturn(asList(new ShortCode().setCodeKey(ShortCode.STOP).setCodes(asList("stop"))));
        setField(parser, "allShortCodes", allShortCodes);
        setField(parser, "allProgramTypes", allProgramTypes);
    }

    @Test
    public void ShouldParseStopMessageWithoutProgram() {
        String messageText = "stop";
        SMS sms = parser.parse(messageText, senderMobileNumber);

        verify(allProgramTypes, never()).findByCampaignShortCode(anyString());
        assertMobileNumberAndMessage(sms, messageText);
        assertNull(sms.getDomain());

        reset(allProgramTypes);

        messageText = "stop ";
        sms = parser.parse(messageText, senderMobileNumber);

        verify(allProgramTypes, never()).findByCampaignShortCode(anyString());
        assertMobileNumberAndMessage(sms, messageText);
        assertNull(sms.getDomain());
    }

    @Test
    public void ShouldParseStopMessageWithProgram() {
        String messageText = "stop p";
        ProgramType programType = mock(ProgramType.class);
        when(allProgramTypes.findByCampaignShortCode("p")).thenReturn(programType);

        SMS sms = parser.parse(messageText, senderMobileNumber);
        assertMobileNumberAndMessage(sms, messageText);
        assertEquals(programType, sms.getDomain());

        reset(allProgramTypes);

        messageText = "stop c";
        programType = mock(ProgramType.class);
        when(allProgramTypes.findByCampaignShortCode("c")).thenReturn(programType);

        sms = parser.parse(messageText, senderMobileNumber);
        assertMobileNumberAndMessage(sms, messageText);
        assertEquals(programType, sms.getDomain());
    }

    @Test
    public void ShouldParseStopMessageWithProgramCaseInsensitive() {
        String messageText = "sToP P";
        ProgramType programType = mock(ProgramType.class);
        when(allProgramTypes.findByCampaignShortCode("P")).thenReturn(programType);

        SMS sms = parser.parse(messageText, senderMobileNumber);
        assertMobileNumberAndMessage(sms, messageText);
        assertEquals(programType, sms.getDomain());

        reset(allProgramTypes);

        messageText = "StoP c";
        programType = mock(ProgramType.class);
        when(allProgramTypes.findByCampaignShortCode("c")).thenReturn(programType);

        sms = parser.parse(messageText, senderMobileNumber);
        assertMobileNumberAndMessage(sms, messageText);
        assertEquals(programType, sms.getDomain());
    }

    private void assertMobileNumberAndMessage(SMS sms, String message) {
        assertEquals(message, sms.getMessage());
    }
}
