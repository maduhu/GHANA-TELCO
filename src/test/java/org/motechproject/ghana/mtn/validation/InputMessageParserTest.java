package org.motechproject.ghana.mtn.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionTypeBuilder;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.motechproject.ghana.mtn.matchers.SubscriptionTypeMatcher;
import org.motechproject.ghana.mtn.repository.AllSubscriptionTypes;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class InputMessageParserTest {
    InputMessageParser messageParser;
    @Mock
    private AllSubscriptionTypes mockAllSubcriptionTypes;

    @Before
    public void setUp() {
        initMocks(this);
        messageParser = new InputMessageParser(mockAllSubcriptionTypes);
    }

    @Test
    public void ShouldParsePregnancyMessage() {
        int startFrom = 25;
        String inputText = "P " + startFrom;
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withShortCode("P").withProgramName("Pregnancy").withMinWeek(5).withMaxWeek(35).build();

        when(mockAllSubcriptionTypes.findByCampaignShortCode("P")).thenReturn(subscriptionType);

        Subscription subscription = messageParser.parse(inputText);

        assertThat(subscription.getSubscriptionType(), new SubscriptionTypeMatcher(subscriptionType));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(startFrom));
    }

    @Test
    public void ShouldParseChildCareMessage() {
        String inputText = "C 25";
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withShortCode("C").withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build();

        when(mockAllSubcriptionTypes.findByCampaignShortCode("C")).thenReturn(subscriptionType);
        Subscription subscription = messageParser.parse(inputText);

        assertThat(subscription.getSubscriptionType(), new SubscriptionTypeMatcher(subscriptionType));
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(25));
    }

    @Test
    public void ShouldParseMessagesEvenWithLowerCase() {
        String inputText = "c 25";
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withShortCode("c").withProgramName("Child Care").withMinWeek(5).withMaxWeek(35).build();

        when(mockAllSubcriptionTypes.findByCampaignShortCode("c")).thenReturn(subscriptionType);

        Subscription subscription = messageParser.parse(inputText);
        assertThat(subscription.getSubscriptionType(), is(subscriptionType));
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
        assertThat(subscription.getStatus(), is(SubscriptionStatus.ACTIVE));

    }

    @Test
    public void ShouldCreateSubscriptionForWeekWithSingleDigit() {
        Subscription subscription = messageParser.parse("P 5");
        assertThat(subscription.getStartWeekAndDay().getWeek().getNumber(), is(5));
    }
}
