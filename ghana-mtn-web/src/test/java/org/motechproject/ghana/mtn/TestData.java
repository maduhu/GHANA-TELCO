package org.motechproject.ghana.mtn;

import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.vo.Money;

public class TestData {

    public static ProgramType pregnancyProgramType() {
        return new ProgramTypeBuilder()
                .withProgramKey(IProgramType.PREGNANCY)
                .withFee(new Money(0.60D))
                .withMinWeek(1).withMaxWeek(53)
                .withProgramName("Child Care")
                .withShortCode("C").withShortCode("c").build();
    }

    public static ProgramType childProgramType() {
        return new ProgramTypeBuilder()
                .withProgramKey(IProgramType.CHILDCARE)
                .withFee(new Money(0.70D)).withMinWeek(5)
                .withMaxWeek(52).withProgramName("Pregnancy").withShortCode("P").withShortCode("p").build();
    }
}
