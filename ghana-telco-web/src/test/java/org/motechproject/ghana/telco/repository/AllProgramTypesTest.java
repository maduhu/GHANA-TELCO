package org.motechproject.ghana.telco.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.telco.BaseSpringTestContext;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.builder.ProgramTypeBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.telco.domain.ProgramType.CHILDCARE;
import static org.motechproject.ghana.telco.domain.ProgramType.PREGNANCY;

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
                .withProgramKey(PREGNANCY)
                .withShortCode("P").build();
        ProgramType childCareProgramType = new ProgramTypeBuilder()
                .withMinWeek(1)
                .withMaxWeek(52)
                .withProgramKey(CHILDCARE)
                .withProgramName("Child Care")
                .withShortCode("C").build();

        allProgramTypes.add(pregnancyProgramType);
        allProgramTypes.add(childCareProgramType);

    }

    @Test
    public void ShouldReturnPregnancyProgramTypeForShortCodeP() {
        ProgramType programType = allProgramTypes.findByCampaignShortCode("P");
        assertThat(programType.getProgramKey(), is(PREGNANCY));
        assertThat(allProgramTypes.findByCampaignShortCode("p").getProgramKey(), is(PREGNANCY));
        assertThat(allProgramTypes.findByCampaignShortCode("c").getProgramKey(), is(CHILDCARE));
        assertThat(allProgramTypes.findByCampaignShortCode("C").getProgramKey(), is(CHILDCARE));
    }

    @After
    public void destroy() {
        allProgramTypes.removeAll();
    }
}
