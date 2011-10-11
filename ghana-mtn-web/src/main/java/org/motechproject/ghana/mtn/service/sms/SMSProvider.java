package org.motechproject.ghana.mtn.service.sms;

public interface SMSProvider {

    boolean send(String mobileNumber, String payload);
}
