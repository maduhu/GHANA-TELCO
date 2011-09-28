package org.motechproject.ghana.mtn.validation;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class InputMessageParserTest {
    InputMessageParser messageParser;

    @Before
    public void setUp() {
      messageParser = new InputMessageParser();
    }

    @Test
    public void ShouldParsePregnancyMessage() {
        String inputText = "P 25";
        Subscription subscription = messageParser.parse(inputText);
        assertThat(subscription.getType(), is(SubscriptionType.PREGNANCY));
        assertThat(subscription.getStartWeek().getNumber(), is(25));
    }

    @Test
    public void ShouldParseChildCareMessage() {
        String inputText = "C 25";
        Subscription subscription = messageParser.parse(inputText);
        assertThat(subscription.getType(), is(SubscriptionType.CHILDCARE));
        assertThat(subscription.getStartWeek().getNumber(), is(25));
    }

    @Test
    public void ShouldParseMessagesEvenWithLowerCase() {
        String inputText = "c 25";
        Subscription subscription = messageParser.parse(inputText);
        assertThat(subscription.getType(), is(SubscriptionType.CHILDCARE));
        assertThat(subscription.getStartWeek().getNumber(), is(25));
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
}
