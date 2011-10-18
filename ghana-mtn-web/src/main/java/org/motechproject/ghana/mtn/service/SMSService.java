package org.motechproject.ghana.mtn.service;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.SMSAudit;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceResponse;
import org.motechproject.ghana.mtn.repository.AllSMSAudits;
import org.motechproject.ghana.mtn.sms.SMSProvider;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMSService {
    private final static Logger log = Logger.getLogger(SMSService.class);
    private SMSProvider smsProvider;
    private AllSMSAudits allSMSAudits;

    @Autowired
    public SMSService(SMSProvider smsProvider, AllSMSAudits allSMSAudits) {
        this.smsProvider = smsProvider;
        this.allSMSAudits = allSMSAudits;
    }

    public SMSServiceResponse send(SMSServiceRequest request) {
        String mobileNumber = request.getMobileNumber();
        String message = request.getMessage();
        String program = request.programKey();
        DateTime now = DateUtil.now();

        smsProvider.send(mobileNumber, message);
        log.info("Subscriber: " + mobileNumber + ":" + message + " : @" + now);

        allSMSAudits.add(new SMSAudit(mobileNumber, program, now, message));
        return new SMSServiceResponse();
    }
}
