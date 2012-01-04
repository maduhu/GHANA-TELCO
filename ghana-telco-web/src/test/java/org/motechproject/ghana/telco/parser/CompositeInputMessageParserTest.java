package org.motechproject.ghana.telco.parser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.SMS;
import org.motechproject.ghana.telco.domain.ShortCode;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.telco.exception.MessageParseFailException;
import org.motechproject.ghana.telco.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.telco.repository.AllProgramTypes;
import org.motechproject.ghana.telco.repository.AllShortCodes;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.telco.domain.ShortCode.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class CompositeInputMessageParserTest {
    CompositeInputMessageParser messageParser;
    @Mock
    private AllProgramTypes allProgramTypes;
    @Mock
    protected AllShortCodes allShortCodes;
    private RegisterProgramMessageParser registerProgramMessageParser;
    private DeliveryMessageParser deliveryMessageParser;
    private StopMessageParser stopMessageParser;
    private RetainOrRollOverChildCareMessageParser retainOrRollOverChildCareMessageParser;

    ProgramType pregnancy;
    ProgramType childCare;
    private String senderMobileNumber = "12345";

    @Before
    public void setUp() {
        initMocks(this);

        pregnancy = new ProgramTypeBuilder().withShortCode("p").withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build();
        childCare = new ProgramTypeBuilder().withShortCode("c").withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build();
        when(allProgramTypes.getAll()).thenReturn(asList(pregnancy, childCare));
        mockShortCode(ShortCode.STOP, asList("stop"));
        mockShortCode(DELIVERY, asList("dd"));
        mockShortCode(RETAIN_EXISTING_CHILDCARE_PROGRAM, asList("e"));
        mockShortCode(USE_ROLLOVER_TO_CHILDCARE_PROGRAM, asList("n"));

        registerProgramMessageParser = new RegisterProgramMessageParser();
        stopMessageParser = new StopMessageParser();
        deliveryMessageParser = new DeliveryMessageParser();
        retainOrRollOverChildCareMessageParser = new RetainOrRollOverChildCareMessageParser();
        
        setField(registerProgramMessageParser, "allProgramTypes", allProgramTypes);
        setField(stopMessageParser, "allProgramTypes", allProgramTypes);
        setField(deliveryMessageParser, "allProgramTypes", allProgramTypes);

        setField(stopMessageParser, "allShortCodes", allShortCodes);
        setField(deliveryMessageParser, "allShortCodes", allShortCodes);
        setField(retainOrRollOverChildCareMessageParser, "allShortCodes", allShortCodes);
        messageParser = new CompositeInputMessageParser(registerProgramMessageParser, stopMessageParser, deliveryMessageParser,
                        retainOrRollOverChildCareMessageParser);
    }

    private void mockShortCode(String shortCodeKey, List<String> shortCodes) {
        when(allShortCodes.getShortCodeFor(shortCodeKey))
                .thenReturn(new ShortCode().setCodeKey(shortCodeKey).setCodes(shortCodes));
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
    
    @Test
    public void ShouldParseDeliveryMessage() {
       String message = "dd ";
       SMS sms = messageParser.parse(message, senderMobileNumber);
       assertEquals(message, sms.getMessage());
       assertEquals(DateUtil.today().toDate(), sms.getDomain());
   }

    private void assertMobileNumberAndMessage(SMS sms, String message) {
        assertEquals(message, sms.getMessage());
    }

}
