package org.motechproject.ghana.mtn.domain.dto;

import org.motechproject.ghana.mtn.domain.IProgramType;

public class SMSServiceRequest {
    String mobileNumber;
    String message;
    IProgramType programType;

    public SMSServiceRequest(String mobileNumber, String message) {
        this.mobileNumber = mobileNumber;
        this.message = message;
    }

    public SMSServiceRequest(String mobileNumber, String message, IProgramType programType) {
        this(mobileNumber, message);
        this.programType = programType;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getMessage() {
        return message;
    }

    public IProgramType getProgramType() {
        return programType;
    }

    public String programName() {
        return programType != null ? programType.getProgramName() : null;
    }
}
