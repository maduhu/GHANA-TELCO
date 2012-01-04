package org.motechproject.ghana.telco.matchers;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.motechproject.ghana.telco.domain.ProgramType;

public class ProgramTypeMatcher extends BaseMatcher<ProgramType> {

    private ProgramType programType;

    public ProgramTypeMatcher(ProgramType programType) {
        this.programType = programType;
    }

    @Override
    public boolean matches(Object o) {
        ProgramType programType = (ProgramType) o;
        return new EqualsBuilder()
                .append(programType.getProgramName(), this.programType.getProgramName())
                .append(programType.getProgramKey(), this.programType.getProgramKey())
                .append(programType.getShortCodes(), this.programType.getShortCodes())
                .append(programType.getMinWeek(), this.programType.getMinWeek())
                .append(programType.getMaxWeek(), this.programType.getMaxWeek())
                .isEquals();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(programType.toString());
    }
}
