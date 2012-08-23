package org.motechproject.ghana.telco.domain;

import org.junit.Test;
import org.motechproject.ghana.telco.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.telco.exception.InvalidMonthException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProgramTypeTest {
    @Test
    public void ShouldVerifySubscriptionWeekIsInRangeForPregnancy() {
        ProgramType programType = new ProgramTypeBuilder().withMaxWeek(35).withMinWeek(5).withShortCode("P").withProgramName("Pregnancy").build();
        assertTrue(programType.isInRange(25));
        assertTrue(programType.isInRange(5));
        assertTrue(programType.isInRange(35));
        assertTrue(programType.isInRange(6));

        assertFalse(programType.isInRange(4));
        assertFalse(programType.isInRange(0));
        assertFalse(programType.isInRange(36));
        assertFalse(programType.isInRange(-6));
    }

    @Test
    public void ShouldVerifySubscriptionWeekIsInRangeForChildCare() {
        ProgramType programType = new ProgramTypeBuilder().withMaxWeek(52).withMinWeek(1).withShortCode("C").withProgramName("Child Care").build();
        assertTrue(programType.isInRange(25));
        assertTrue(programType.isInRange(1));
        assertTrue(programType.isInRange(52));
        assertTrue(programType.isInRange(10));

        assertFalse(programType.isInRange(53));
        assertFalse(programType.isInRange(0));
        assertFalse(programType.isInRange(-6));
    }

    @Test
    public void shouldConvertMonthsToWeeks() throws InvalidMonthException {
        ProgramType programType = new ProgramTypeBuilder().withMaxWeek(52).withProgramKey(ProgramType.CHILDCARE).withMinWeek(1).withShortCode("C").withProgramName("Child Care").build();
        assertThat(programType.weekFor(10), is(equalTo(39)));

        ProgramType pregnancyProgramType = new ProgramTypeBuilder().withMaxWeek(52).withProgramKey(ProgramType.PREGNANCY).withMinWeek(1).withShortCode("C").withProgramName("Child Care").build();
        assertThat(pregnancyProgramType.weekFor(10), is(equalTo(10)));
    }

    @Test(expected = InvalidMonthException.class)
    public void shouldThrowIllegalArgumentExceptionInCaseOfInvalidMonthNumberForChildCare() throws InvalidMonthException {
        ProgramType programType = new ProgramTypeBuilder().withMaxWeek(52).withProgramKey(ProgramType.CHILDCARE).withMinWeek(1).withShortCode("C").withProgramName("Child Care").build();
        assertThat(programType.weekFor(50), is(equalTo(39)));
    }
}
