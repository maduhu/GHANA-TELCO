package org.motechproject.ghana.mtn.domain;

import org.motechproject.ghana.mtn.service.SMSHandler;

public class StopSMS extends SMS<IProgramType> {

    public StopSMS(String message, IProgramType domain) {
        super(message, domain);
    }

    @Override
    public void process(SMSHandler handler) {
        handler.stop(this);
    }
}
