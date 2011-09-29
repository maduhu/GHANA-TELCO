package org.motechproject.ghana.mtn.domain.builder;

import org.junit.Test;
import org.motechproject.ghana.mtn.domain.SubscriptionType;

import static org.junit.Assert.assertThat;

import static org.hamcrest.core.Is.is;

public class SubscriptionTypeBuilderTest {
    @Test
    public void ShouldCreateSubscriptionTypeObjectUsingBuilder() {
        Integer minWeek = 5;
        Integer maxWeek = 10;
        String programName = "Pregnancy";
        String shortCode = "P";
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder()
                .withProgramName(programName)
                .withShortCode(shortCode)
                .withMinWeek(minWeek).withMaxWeek(maxWeek).build();

        assertThat(subscriptionType.getMaxWeek(), is(maxWeek));
        assertThat(subscriptionType.getMinWeek(), is(minWeek));
        assertThat(subscriptionType.getProgramName(), is(programName));
        assertThat(subscriptionType.getShortCodes().get(0), is(shortCode));
    }
}
