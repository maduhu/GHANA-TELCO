package org.motechproject.ghana.telco;

import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.builder.ProgramTypeBuilder;

public class TestData {

    public static ProgramTypeBuilder pregnancyProgramType() {
        return new ProgramTypeBuilder()
                .withProgramKey(ProgramType.PREGNANCY)
                .withMinWeek(1).withMaxWeek(35)
                .withProgramName("Pregnancy")
                .withShortCode("C").withShortCode("c");
    }

    public static ProgramTypeBuilder childProgramType() {
        return new ProgramTypeBuilder()
                .withProgramKey(ProgramType.CHILDCARE)
                .withMinWeek(5)
                .withMaxWeek(52).withProgramName("Child Care").withShortCode("P").withShortCode("p");
    }
}
