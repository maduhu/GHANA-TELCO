package org.motechproject.ghana.mtn.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.BaseSpringTestContext;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AllProgramTypesTest extends BaseSpringTestContext {

    @Autowired
    private AllProgramTypes allProgramTypes;

    @Before
    public void setUp() {
        createDefaultValues();
    }

    private void createDefaultValues() {
        ProgramType pregnancyProgramType = new ProgramTypeBuilder()
                .withMinWeek(5)
                .withMaxWeek(35)
                .withProgramName("Pregnancy")
                .withShortCode("P").build();
        ProgramType childCareProgramType = new ProgramTypeBuilder()
                .withMinWeek(1)
                .withMaxWeek(52)
                .withProgramName("Child Care")
                .withShortCode("C").build();

        allProgramTypes.add(pregnancyProgramType);
        allProgramTypes.add(childCareProgramType);

    }

    @Test
    public void ShouldReturnPregnancyProgramTypeForShortCodeP() {
        ProgramType programType = allProgramTypes.findByCampaignShortCode("P");
        assertThat(programType.getProgramName(), is("Pregnancy"));
        assertThat(allProgramTypes.findByCampaignShortCode("p").getProgramName(), is("Pregnancy"));
        assertThat(allProgramTypes.findByCampaignShortCode("c").getProgramName(), is("Child Care"));
        assertThat(allProgramTypes.findByCampaignShortCode("C").getProgramName(), is("Child Care"));
    }

    @After
    public void destroy() {
        remove(allProgramTypes.getAll());
    }
}
