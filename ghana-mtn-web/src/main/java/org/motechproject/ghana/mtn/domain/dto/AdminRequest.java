package org.motechproject.ghana.mtn.domain.dto;

import org.motechproject.ghana.mtn.domain.ProgramType;

import java.util.List;

public class AdminRequest {

    private List<ProgramType> programTypes;

    public List<ProgramType> getProgramTypes() {
        return programTypes;
    }

    public void setProgramTypes(List<ProgramType> programTypes) {
        this.programTypes = programTypes;
    }
}
