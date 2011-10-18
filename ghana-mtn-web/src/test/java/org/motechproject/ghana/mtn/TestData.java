package org.motechproject.ghana.mtn;

import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.vo.Money;

public class TestData {

    public static ProgramTypeBuilder pregnancyProgramType() {
        return new ProgramTypeBuilder()
                .withProgramKey(IProgramType.PREGNANCY)
                .withFee(new Money(0.60D))
                .withMinWeek(1).withMaxWeek(35)
                .withProgramName("Child Care")
                .withShortCode("C").withShortCode("c");
    }

    public static ProgramTypeBuilder childProgramType() {
        return new ProgramTypeBuilder()
                .withProgramKey(IProgramType.CHILDCARE)
                .withFee(new Money(0.70D)).withMinWeek(5)
                .withMaxWeek(52).withProgramName("Pregnancy").withShortCode("P").withShortCode("p");
    }
}
