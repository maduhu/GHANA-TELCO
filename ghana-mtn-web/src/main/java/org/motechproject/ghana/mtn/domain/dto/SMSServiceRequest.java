package org.motechproject.ghana.mtn.domain.dto;

import org.motechproject.ghana.mtn.domain.ProgramType;

public class SMSServiceRequest {
    String mobileNumber;
    String message;
    ProgramType programType;

    public SMSServiceRequest(String mobileNumber, String message) {
        this.mobileNumber = mobileNumber;
        this.message = message;
    }

    public SMSServiceRequest(String mobileNumber, String message, ProgramType programType) {
        this(mobileNumber, message);
        this.programType = programType;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getMessage() {
        return message;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public String programKey() {
        return programType != null ? programType.getProgramKey() : null;
    }
}
