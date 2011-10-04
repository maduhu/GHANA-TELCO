package org.motechproject.ghana.mtn.tools.seed;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramTypeSeed extends Seed {
    private Logger log = Logger.getLogger(ProgramTypeSeed.class);
    @Autowired
    private AllProgramTypes allProgramTypes;

    @Override
    protected void load() {
        ProgramType pregnancyProgramType = new ProgramTypeBuilder()
                .withProgramName("Pregnancy")
                .withShortCode("P")
                .withShortCode("p")
                .withMaxWeek(35).withMinWeek(5).build();

        ProgramType childCareProgramType = new ProgramTypeBuilder()
                .withProgramName("Child Care")
                .withShortCode("C")
                .withShortCode("c")
                .withMaxWeek(52).withMinWeek(1).build();

        allProgramTypes.add(pregnancyProgramType);
        allProgramTypes.add(childCareProgramType);
    }
}