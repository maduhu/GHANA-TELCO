package org.motechproject.ghana.telco.domain;

import org.motechproject.ghana.telco.service.SMSHandler;

public class RetainOrRollOverChildCareProgramSMS extends SMS<Boolean> {

    public RetainOrRollOverChildCareProgramSMS(String message, Boolean domain) {
        super(message, domain);
    }

    @Override
    public void process(SMSHandler handler) {
        handler.retainOrRollOverChildCare(this);
    }

    public Boolean retainExistingChildCareProgram() {
        return getDomain();
    }
}
