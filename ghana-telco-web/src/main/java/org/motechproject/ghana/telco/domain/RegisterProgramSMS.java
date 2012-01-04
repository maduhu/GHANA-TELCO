package org.motechproject.ghana.telco.domain;

import org.motechproject.ghana.telco.service.SMSHandler;

public class RegisterProgramSMS extends SMS<Subscription> {

    public RegisterProgramSMS(String message, Subscription domain) {
        super(message, domain);
    }

    @Override
    public void process(SMSHandler handler) {
        handler.register(this);
    }
}
