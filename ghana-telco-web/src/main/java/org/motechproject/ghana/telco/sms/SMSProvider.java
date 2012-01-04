package org.motechproject.ghana.telco.sms;

import org.motechproject.model.Time;

public interface SMSProvider {

    boolean send(String mobileNumber, String payload, Time deliveryTime);
}
