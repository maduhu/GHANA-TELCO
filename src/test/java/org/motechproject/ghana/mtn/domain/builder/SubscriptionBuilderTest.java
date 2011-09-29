package org.motechproject.ghana.mtn.domain.builder;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.vo.Week;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class SubscriptionBuilderTest {
    @Test
    public void ShouldCreateSubscriptionUsingBuilder() {
        DateTime dateTime = new DateTime();
        Integer weekNumber = 5;
        Subscription subscription = new SubscriptionBuilder().withRegistrationDate(dateTime).withStartWeek(new Week(weekNumber)).withStatus(SubscriptionStatus.ACTIVE).build();

        assertThat(subscription. getRegistrationDate(), is(dateTime));
        assertThat(subscription.getStartWeek().getNumber(), is(weekNumber));
        assertNull(subscription.getSubscriptionType());
    }
}
