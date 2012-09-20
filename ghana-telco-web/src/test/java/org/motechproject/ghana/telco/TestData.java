package org.motechproject.ghana.telco;

import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.builder.ProgramTypeBuilder;

public class TestData {

    public static ProgramTypeBuilder childProgramType() {
        return new ProgramTypeBuilder()
                .withProgramKey(ProgramType.CHILDCARE)
                .withMinWeek(1).withMaxWeek(52)
                .withProgramName("              ")
                .withShortCode("C").withShortCode("c");
    }

    public static ProgramTypeBuilder pregnancyProgramType() {
        return new ProgramTypeBuilder()
                .withProgramKey(ProgramType.PREGNANCY)
                .withMinWeek(5)
                .withMaxWeek(35).withProgramName("Child Care").withShortCode("P").withShortCode("p");
    }
}
