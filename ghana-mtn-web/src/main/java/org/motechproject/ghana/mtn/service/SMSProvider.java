package org.motechproject.ghana.mtn.service;

public interface SMSProvider {

    boolean send(String mobileNumber, String payload);
}
