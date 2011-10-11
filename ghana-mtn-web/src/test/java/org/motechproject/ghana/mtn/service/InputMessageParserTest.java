package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;

import java.util.Arrays;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class InputMessageParserTest {
    InputMessageParser messageParser;
    @Mock
    private AllProgramTypes allProgramTypes;

    @Before
    public void setUp() {
        initMocks(this);
        messageParser = new InputMessageParser(allProgramTypes);
    }

    @Test
    public void ShouldParsePregnancyMessage() {
        int startFrom = 25;
        String inputText = "P " + startFrom;
        ProgramType programType = new ProgramTypeBuilder().withShortCode("P").withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build();

        when(allProgramTypes.findByCampaignShortCode("P")).thenReturn(programType);

        Subscription subscription = messageParser.parse(inputText);

        assertThat(subscription.getProgramType(), new ProgramTypeMatcher(programType));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(startFrom));
    }

    @Test
    public void ShouldParseChildCareMessage() {
        String inputText = "C 25";
        ProgramType programType = new ProgramTypeBuilder().withShortCode("C").withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build();

        when(allProgramTypes.findByCampaignShortCode("C")).thenReturn(programType);
        Subscription subscription = messageParser.parse(inputText);

        assertThat(subscription.getProgramType(), new ProgramTypeMatcher(programType));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(25));
    }

    @Test
    public void ShouldParseMessagesEvenWithLowerCase() {
        String inputText = "c 25";
        ProgramType programType = new ProgramTypeBuilder().withShortCode("c").withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build();

        when(allProgramTypes.findByCampaignShortCode("C")).thenReturn(programType);

        Subscription subscription = messageParser.parse(inputText);
        assertThat(subscription.getProgramType(), is(programType));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(25));
    }

    @Test(expected = MessageParseFailException.class)
    public void ShouldFailForMessagesThatAreNotValid() {
        String inputText = "q 25";
        messageParser.parse(inputText);
    }

    @Test
    public void ShouldCreateSubscriptionWithActiveStatusForValidInputMessage() {
        Subscription subscription = messageParser.parse("P 10");
        assertNull(subscription.getStatus());

    }

    @Test
    public void ShouldCreateSubscriptionForWeekWithSingleDigit() {
        Subscription subscription = messageParser.parse("P 5");
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(5));
    }

    @Test
    public void ShouldParseBasedOnNewShortCodes() {
        String shortCode = "CHI";
        ProgramType childCareProgramType = new ProgramTypeBuilder().withShortCode(shortCode).withShortCode("C").withProgramName("ChildCare").withMinWeek(5).withMaxWeek(35).build();

        when(allProgramTypes.findByCampaignShortCode(shortCode)).thenReturn(childCareProgramType);
        when(allProgramTypes.getAll()).thenReturn(Arrays.asList(childCareProgramType));

        messageParser.recompilePattern();
        Subscription actualSubscription = messageParser.parse(shortCode + " 5");

        assertThat(actualSubscription.getProgramType(), new ProgramTypeMatcher(childCareProgramType));
    }
}
