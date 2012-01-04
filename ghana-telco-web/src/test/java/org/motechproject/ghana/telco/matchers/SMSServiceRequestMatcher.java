package org.motechproject.ghana.telco.matchers;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.dto.SMSServiceRequest;

public class SMSServiceRequestMatcher extends BaseMatcher<SMSServiceRequest> {

    String mobileNumber;
    String message;
    ProgramType programType;

    public SMSServiceRequestMatcher(String mobileNumber, String message, ProgramType programType) {
        this.mobileNumber = mobileNumber;
        this.message = message;
        this.programType = programType;
    }

    @Override
    public boolean matches(Object argument) {
        SMSServiceRequest request = (SMSServiceRequest) argument;
        return new EqualsBuilder().append(mobileNumber, request.getMobileNumber())
                .append(message, request.getMessage())
                .append(programKey(programType), programKey(request.getProgramType()))
                .isEquals();  
    }

    private String programKey(ProgramType programType) {
        return programType != null ? programType.getProgramKey() : null;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("SMSServiceRequest comparison failed");                                                
    }
}
