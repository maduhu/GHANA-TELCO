package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceResponse;

public interface SMSService {

    SMSServiceResponse send(SMSServiceRequest smsServiceRequest);
}
