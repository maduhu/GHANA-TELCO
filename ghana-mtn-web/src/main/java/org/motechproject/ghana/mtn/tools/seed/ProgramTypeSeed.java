package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
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
                .withProgramKey(ProgramType.CHILDCARE)
                .build();

        allProgramTypes.add(childCareProgramType);

        ProgramType pregnancyProgramType = new ProgramTypeBuilder()
                .withProgramName("Pregnancy")
                .withShortCode("P").withShortCode("p")
                .withMaxWeek(35).withMinWeek(5)
                .withRollOverProgramType(childCareProgramType)
                .withProgramKey(ProgramType.PREGNANCY)
                .build();

        allProgramTypes.add(pregnancyProgramType);
    }
}
