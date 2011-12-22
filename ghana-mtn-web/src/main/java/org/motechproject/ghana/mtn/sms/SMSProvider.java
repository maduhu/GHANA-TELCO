package org.motechproject.ghana.mtn.sms;

import org.motechproject.model.Time;

public interface SMSProvider {

    boolean send(String mobileNumber, String payload, Time deliveryTime);
}
