package org.motechproject.ghana.mtn.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.motechproject.ghana.mtn.domain.ProgramType;

public class ProgramTypeMatcher extends BaseMatcher<ProgramType> {

    private ProgramType programType;

    public ProgramTypeMatcher(ProgramType programType) {
        this.programType = programType;
    }

    @Override
    public boolean matches(Object o) {
        ProgramType programType = (ProgramType) o;
        return programType.getProgramName().equals(this.programType.getProgramName())
                && programType.getShortCodes().equals(this.programType.getShortCodes())
                && programType.getMinWeek().equals(this.programType.getMinWeek())
                && programType.getMaxWeek().equals(this.programType.getMaxWeek());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(programType.toString());
    }
}
