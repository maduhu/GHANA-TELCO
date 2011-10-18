package org.motechproject.ghana.mtn.domain;

import org.motechproject.ghana.mtn.service.SMSHandler;

import java.util.Date;

public class DeliverySMS extends SMS<Date> {

    public DeliverySMS(String message, Date domain) {
        super(message, domain);
    }

    @Override
    public void process(SMSHandler handler) {        
    }
}
