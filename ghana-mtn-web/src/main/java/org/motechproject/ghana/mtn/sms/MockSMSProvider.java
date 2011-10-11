package org.motechproject.ghana.mtn.sms;

import org.springframework.stereotype.Component;

@Component
public class MockSMSProvider implements SMSProvider {

    @Override
    public boolean send(String mobileNumber, String payload) {
        return true;
    }
}
