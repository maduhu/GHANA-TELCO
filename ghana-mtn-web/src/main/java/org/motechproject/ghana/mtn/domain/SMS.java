package org.motechproject.ghana.mtn.domain;

import org.motechproject.ghana.mtn.service.SMSHandler;

public abstract class SMS<T> {

    String message;
    String fromMobileNumber;
    T domain;

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

    public String getFromMobileNumber() {
        return fromMobileNumber;
    }

    public String getMessage() {
        return message;
    }

    public abstract void process(SMSHandler handler);

    public static class RegisterProgramSMS extends SMS<Subscription> {

        public RegisterProgramSMS(String message, Subscription domain) {
            super(message, domain);
        }

        @Override
        public void process(SMSHandler handler) {
            handler.register(this);
        }
    }

    public static class StopSMS extends SMS<IProgramType> {
        public StopSMS(String message, IProgramType domain) {
            super(message, domain);
        }

        @Override
        public void process(SMSHandler handler) {
            handler.stop(this);
        }
    }
}


