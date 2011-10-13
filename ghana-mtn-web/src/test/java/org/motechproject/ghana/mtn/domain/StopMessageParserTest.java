package org.motechproject.ghana.mtn.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class StopMessageParserTest {

    StopMessageParser parser;
    @Mock
    private AllProgramTypes allProgramTypes;

    ProgramType pregnancy;
    ProgramType childCare;

    @Before
    public void setUp() {
        initMocks(this);

        pregnancy = new ProgramTypeBuilder().withShortCode("p").withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build();
        childCare = new ProgramTypeBuilder().withShortCode("c").withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build();
        when(allProgramTypes.getAll()).thenReturn(asList(pregnancy, childCare));
        parser = new StopMessageParser(allProgramTypes);
    }

    @Test
    public void ShouldParseStopMessageWithoutProgram() {
        String messageText = "stop ";
        SMS sms = parser.parse(messageText);

        verify(allProgramTypes, never()).findByCampaignShortCode(anyString());
        assertMobileNumberAndMessage(sms, messageText);
        assertNull(sms.getDomain());
    }

    @Test
    public void ShouldParseStopMessageWithProgram() {
        String messageText = "stop p";
        ProgramType programType = mock(ProgramType.class);
        when(allProgramTypes.findByCampaignShortCode("p")).thenReturn(programType);

        SMS sms = parser.parse(messageText);
        assertMobileNumberAndMessage(sms, messageText);
        assertEquals(programType, sms.getDomain());

        reset(allProgramTypes);

        messageText = "stop c";
        programType = mock(ProgramType.class);
        when(allProgramTypes.findByCampaignShortCode("c")).thenReturn(programType);

        sms = parser.parse(messageText);
        assertMobileNumberAndMessage(sms, messageText);
        assertEquals(programType, sms.getDomain());
    }

    @Test
    public void ShouldParseStopMessageWithProgramCaseInsensitive() {
        String messageText = "sToP P";
        ProgramType programType = mock(ProgramType.class);
        when(allProgramTypes.findByCampaignShortCode("P")).thenReturn(programType);

        SMS sms = parser.parse(messageText);
        assertMobileNumberAndMessage(sms, messageText);
        assertEquals(programType, sms.getDomain());

        reset(allProgramTypes);

        messageText = "StoP c";
        programType = mock(ProgramType.class);
        when(allProgramTypes.findByCampaignShortCode("c")).thenReturn(programType);

        sms = parser.parse(messageText);
        assertMobileNumberAndMessage(sms, messageText);
        assertEquals(programType, sms.getDomain());
    }

    private void assertMobileNumberAndMessage(SMS sms, String message) {
        assertNull(sms.getFromMobileNumber());
        assertEquals(message, sms.getMessage());
    }
}
