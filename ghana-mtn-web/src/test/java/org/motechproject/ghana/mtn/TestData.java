package org.motechproject.ghana.mtn;

import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;

public class TestData {

    public static ProgramTypeBuilder pregnancyProgramType() {
        return new ProgramTypeBuilder()
                .withProgramKey(ProgramType.PREGNANCY)
                .withMinWeek(1).withMaxWeek(35)
                .withProgramName("Child Care")
                .withShortCode("C").withShortCode("c");
    }

    public static ProgramTypeBuilder childProgramType() {
        return new ProgramTypeBuilder()
                .withProgramKey(ProgramType.CHILDCARE)
                .withMinWeek(5)
                .withMaxWeek(52).withProgramName("Pregnancy").withShortCode("P").withShortCode("p");
    }
}
