package org.motechproject.ghana.mtn.domain;

import org.junit.Test;
import org.mockito.internal.matchers.Null;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SubscriptionTypeTest {
    @Test
    public void ShouldCreatePregnancySubcriptionTypeForInputStringP() {
        SubscriptionType type = SubscriptionType.fromString("P");
        assertThat(type, is(SubscriptionType.PREGNANCY));
    }

    @Test
    public void ShouldCreateChildCareSubscriptionTypeForInputStringC() {
        SubscriptionType type = SubscriptionType.fromString("C");
        assertThat(type, is(SubscriptionType.CHILDCARE));
    }

    @Test
    public void ShouldCreateChildCareSubscriptionTypeForInputStringLowerCaseP() {
        SubscriptionType type = SubscriptionType.fromString("p");
        assertThat(type, is(SubscriptionType.PREGNANCY));
    }

    @Test
    public void ShouldCreateChildCareSubscriptionTypeForInputStringLowerCaseC() {
        SubscriptionType type = SubscriptionType.fromString("c");
        assertThat(type, is(SubscriptionType.CHILDCARE));
    }

    @Test
    public void ShouldReturnNullForInvalidInputString() {
        SubscriptionType type = SubscriptionType.fromString("X");
        assertThat(type, is(Null.NULL));
    }

    @Test
    public void ShouldVerifySubscriptionWeekIsInRangeForPregnancy() {
        SubscriptionType type = SubscriptionType.PREGNANCY;
        assertTrue(type.isInRange(25));
        assertTrue(type.isInRange(5));
        assertTrue(type.isInRange(35));
        assertTrue(type.isInRange(6));

        assertFalse(type.isInRange(4));
        assertFalse(type.isInRange(0));
        assertFalse(type.isInRange(36));
        assertFalse(type.isInRange(-6));
    }

    @Test
    public void ShouldVerifySubscriptionWeekIsInRangeForChildCare() {
        SubscriptionType type = SubscriptionType.CHILDCARE;
        assertTrue(type.isInRange(25));
        assertTrue(type.isInRange(1));
        assertTrue(type.isInRange(52));
        assertTrue(type.isInRange(10));

        assertFalse(type.isInRange(53));
        assertFalse(type.isInRange(0));
        assertFalse(type.isInRange(-6));
    }
}
