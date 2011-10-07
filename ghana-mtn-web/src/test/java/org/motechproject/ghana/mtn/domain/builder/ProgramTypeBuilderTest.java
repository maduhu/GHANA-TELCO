package org.motechproject.ghana.mtn.domain.builder;

import org.junit.Test;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.dto.Money;

import static org.junit.Assert.assertThat;

import static org.hamcrest.core.Is.is;

public class ProgramTypeBuilderTest {
    @Test
    public void ShouldCreateProgramTypeObjectUsingBuilder() {
        Integer minWeek = 5;
        Integer maxWeek = 10;
        String programName = "Pregnancy";
        String shortCode = "P";
        Money fee = new Money(0.75D);
        ProgramType programType = new ProgramTypeBuilder()
                .withProgramName(programName)
                .withShortCode(shortCode)
                .withMinWeek(minWeek).withFee(fee).withMaxWeek(maxWeek).build();

        assertThat(programType.getMaxWeek(), is(maxWeek));
        assertThat(programType.getMinWeek(), is(minWeek));
        assertThat(programType.getFee(), is(fee));
        assertThat(programType.getProgramName(), is(programName));
        assertThat(programType.getShortCodes().get(0), is(shortCode));
    }
}
