package org.motechproject.ghana.mtn.billing.dto;

import org.motechproject.ghana.mtn.domain.IProgramType;

public class BillingServiceRequest {
    private String mobileNumber;
    private IProgramType programType;

    public BillingServiceRequest(String mobileNumber, IProgramType programType) {
        this.mobileNumber = mobileNumber;
        this.programType = programType;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public IProgramType getProgramType() {
        return programType;
    }

    public String programName() {
        return programType.getProgramName();
    }

    public Double getFeeForProgram() {
        return programType.getFee().getValue();
    }
}