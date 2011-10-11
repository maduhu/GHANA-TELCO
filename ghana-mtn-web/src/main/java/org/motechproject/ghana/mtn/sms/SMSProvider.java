package org.motechproject.ghana.mtn.sms;

public interface SMSProvider {

    boolean send(String mobileNumber, String payload);
}
