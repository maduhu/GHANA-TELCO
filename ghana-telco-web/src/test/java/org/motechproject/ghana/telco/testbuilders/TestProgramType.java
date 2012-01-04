package org.motechproject.ghana.telco.testbuilders;

import org.motechproject.ghana.telco.domain.ProgramType;

import java.util.List;

public class TestProgramType {

     public static ProgramType with(String programName, Integer minWeek, Integer maxWeek, List<String> shortCodes){
        ProgramType programType = new ProgramType();
        programType.setMaxWeek(maxWeek);
        programType.setMinWeek(minWeek);
        programType.setProgramName(programName);
        programType.setShortCodes(shortCodes);
        return programType;
    }
}
