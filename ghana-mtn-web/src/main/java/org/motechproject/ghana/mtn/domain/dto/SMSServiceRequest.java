package org.motechproject.ghana.mtn.domain.dto;

import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.model.Time;

public class SMSServiceRequest {
    String mobileNumber;
    String message;
    Time deliveryTime;
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

    public void setDeliveryTime(Time deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Time getDeliveryTime() {
        return deliveryTime;
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
