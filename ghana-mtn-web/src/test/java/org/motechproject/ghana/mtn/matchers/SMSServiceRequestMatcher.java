package org.motechproject.ghana.mtn.matchers;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;

public class SMSServiceRequestMatcher extends BaseMatcher<SMSServiceRequest> {

    String mobileNumber;
    String message;
    IProgramType programType;

    public SMSServiceRequestMatcher(String mobileNumber, String message, IProgramType programType) {
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

    private String programKey(IProgramType programType) {
        return programType != null ? programType.getProgramKey() : null;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("SMSServiceRequest comparison failed");                                                
    }
}
