package org.motechproject.ghana.mtn.parser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class InputMessageParserTest {
    InputMessageParser messageParser;
    @Mock
    private AllProgramTypes allProgramTypes;
    private RegisterProgramMessageParser registerProgramMessageParser;
    private StopMessageParser stopMessageParser;

    ProgramType pregnancy;
    ProgramType childCare;
    private String senderMobileNumber = "12345";

    @Before
    public void setUp() {
        initMocks(this);

        pregnancy = new ProgramTypeBuilder().withShortCode("p").withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build();
        childCare = new ProgramTypeBuilder().withShortCode("c").withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build();
        when(allProgramTypes.getAll()).thenReturn(asList(pregnancy, childCare));
        registerProgramMessageParser = new RegisterProgramMessageParser(allProgramTypes);
        stopMessageParser = new StopMessageParser(allProgramTypes);
        messageParser = new InputMessageParser(registerProgramMessageParser, stopMessageParser);
    }

    @Test
    public void ShouldParsePregnancyMessage() {
        int startFrom = 25;
        String messageText = "P " + startFrom;

        when(allProgramTypes.findByCampaignShortCode("P")).thenReturn(pregnancy);

        SMS sms = messageParser.parse(messageText, senderMobileNumber);
        Subscription subscription = (Subscription) sms.getDomain();

        assertMobileNumberAndMessage(sms, messageText);
        assertThat(subscription.getProgramType(), new ProgramTypeMatcher(pregnancy));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(startFrom));
    }

    @Test
    public void ShouldParseChildCareMessage() {
        String messageText = "C 25";

        when(allProgramTypes.findByCampaignShortCode("C")).thenReturn(childCare);
        SMS sms = messageParser.parse(messageText, senderMobileNumber);
        Subscription subscription = (Subscription) sms.getDomain();

        assertMobileNumberAndMessage(sms, messageText);
        assertThat(subscription.getProgramType(), new ProgramTypeMatcher(childCare));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(25));
    }

    @Test
    public void ShouldParseMessagesEvenWithLowerCase() {
        String messageText = "c 25";
        ProgramType programType = new ProgramTypeBuilder().withShortCode("c").withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build();

        when(allProgramTypes.findByCampaignShortCode("c")).thenReturn(programType);

        SMS sms = messageParser.parse(messageText, senderMobileNumber);
        Subscription subscription = (Subscription) sms.getDomain();

        assertMobileNumberAndMessage(sms, messageText);
        assertThat(subscription.getProgramType(), is(programType));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(25));
    }

    @Test(expected = MessageParseFailException.class)
    public void ShouldFailForMessagesThatAreNotValid() {
        String messageText = "q 25";
        messageParser.parse(messageText, senderMobileNumber);
    }

    @Test
    public void ShouldCreateSubscriptionWithActiveStatusForValidInputMessage() {
        String messageText = "P 10";
        SMS sms = messageParser.parse(messageText, senderMobileNumber);
        Subscription subscription = (Subscription) sms.getDomain();

        assertMobileNumberAndMessage(sms, messageText);
        assertNull(subscription.getStatus());
    }

    @Test
    public void ShouldCreateSubscriptionForWeekWithSingleDigit() {
        String messageText = "P 5";
        SMS sms = messageParser.parse(messageText, senderMobileNumber);
        Subscription subscription = (Subscription) sms.getDomain();
        assertMobileNumberAndMessage(sms, messageText);
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(5));
    }

    @Test
    public void ShouldParseBasedOnNewShortCodes() {
        String shortCode = "CHI";
        ProgramType childCareProgramType = new ProgramTypeBuilder().withShortCode(shortCode).withShortCode("C").withProgramName("ChildCare").withMinWeek(5).withMaxWeek(35).build();

        when(allProgramTypes.findByCampaignShortCode(shortCode)).thenReturn(childCareProgramType);
        when(allProgramTypes.getAll()).thenReturn(Arrays.asList(childCareProgramType));

        messageParser.recompilePatterns();
        String messageText = shortCode + " 5";
        SMS sms = messageParser.parse(messageText, senderMobileNumber);
        Subscription actualSubscription = (Subscription) sms.getDomain();
        assertMobileNumberAndMessage(sms, messageText);
        assertThat(actualSubscription.getProgramType(), new ProgramTypeMatcher(childCareProgramType));
    }
    
    @Test
    public void ShouldParseStopMessageWithProgramCaseInsensitive() {
        String messageText = "sToP P";
        ProgramType programType = mock(ProgramType.class);
        when(allProgramTypes.findByCampaignShortCode("P")).thenReturn(programType);

        SMS sms = messageParser.parse(messageText, senderMobileNumber);
        assertMobileNumberAndMessage(sms, messageText);
        assertEquals(programType, sms.getDomain());

        reset(allProgramTypes);

        messageText = "StoP c";
        programType = mock(ProgramType.class);
        when(allProgramTypes.findByCampaignShortCode("c")).thenReturn(programType);

        sms = messageParser.parse(messageText, senderMobileNumber);
        assertMobileNumberAndMessage(sms, messageText);
        assertEquals(programType, sms.getDomain());
    }

    private void assertMobileNumberAndMessage(SMS sms, String message) {
        assertEquals(message, sms.getMessage());
    }

}
