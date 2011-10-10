package org.motechproject.ghana.sms.service;

import org.motechproject.ghana.sms.dto.SMSServiceRequest;
import org.motechproject.ghana.sms.dto.SMSServiceResponse;

public interface SMSService {

    SMSServiceResponse send(SMSServiceRequest smsServiceRequest);
}
