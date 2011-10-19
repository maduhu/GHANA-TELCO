package org.motechproject.ghana.mtn.domain;

import org.motechproject.ghana.mtn.service.SMSHandler;

public abstract class SMS<T> {

    String message;
    String fromMobileNumber;
    T domain;
    String referrer;

    public SMS(String message, T domain) {
        this.message = message;
        this.domain = domain;
    }

    public T getDomain() {
        return domain;
    }

    public SMS setFromMobileNumber(String fromMobileNumber) {
        this.fromMobileNumber = fromMobileNumber;
        return this;
    }

    public SMS setReferrer(String referrer) {
        this.referrer = referrer;
        return this;
    }

    public String getFromMobileNumber() {
        return fromMobileNumber;
    }

    public String getMessage() {
        return message;
    }

    public abstract void process(SMSHandler handler);

    public String getReferrer() {
        return referrer;
    }
}


