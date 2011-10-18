package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramTypeSeed extends Seed {
    @Autowired
    private AllProgramTypes allProgramTypes;

    @Override
    protected void load() {

        ProgramType childCareProgramType = new ProgramTypeBuilder()
                .withProgramName("Child Care")
                .withShortCode("C").withShortCode("c")
                .withMaxWeek(52).withMinWeek(1)
                .withProgramKey(IProgramType.CHILDCARE)
                .withFee(new Money(0.60D)).build();

        allProgramTypes.add(childCareProgramType);

        ProgramType pregnancyProgramType = new ProgramTypeBuilder()
                .withProgramName("Pregnancy")
                .withShortCode("P").withShortCode("p")
                .withMaxWeek(35).withMinWeek(5)
                .willRollOverProgramType(childCareProgramType)
                .withProgramKey(IProgramType.PREGNANCY)
                .withFee(new Money(0.60D)).build();

        allProgramTypes.add(pregnancyProgramType);
    }
}
