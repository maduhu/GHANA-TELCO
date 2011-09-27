package org.motechproject.ghana.mtn.validation;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.domain.Subscription;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class InputMessageValidatorTest {
    InputMessageValidator validator;

    @Before
    public void setUp() {
        validator = new InputMessageValidator();
    }

    @Test
    public void ShouldReturnTrueForValidWeekRangeForPregnancy() {
        Subscription subscription = new Subscription("P", "25");
        assertThat(true, is(validator.validate(subscription)));
    }

    @Test
    public void ShouldReturnTrueForValidWeekRangeForChildCare() {
        Subscription subscription = new Subscription("C", "50");
        assertThat(true, is(validator.validate(subscription)));
    }

    @Test
    public void ShouldReturnFalseForInValidWeekRangeForPregnancy() {
        Subscription subscription = new Subscription("P", "37");
        assertThat(false, is(validator.validate(subscription)));
    }

    @Test
    public void ShouldReturnFalseForInValidWeekRangeForChildCare() {
        Subscription subscription = new Subscription("C", "53");
        assertThat(false, is(validator.validate(subscription)));
    }
}
