package org.motechproject.ghana.mtn.service;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.SMSAudit;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceResponse;
import org.motechproject.ghana.mtn.repository.AllProgramMessageAudits;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.motechproject.util.DateUtil.now;

@Service
public class SMSService {

    private final static Logger log = Logger.getLogger(SMSService.class);
    private SMSProvider smsProviderService;
    private AllProgramMessageAudits allProgramMessageAudits;

    @Autowired
    public SMSService(SMSProvider smsProviderService, AllProgramMessageAudits allProgramMessageAudits) {
        this.smsProviderService = smsProviderService;
        this.allProgramMessageAudits = allProgramMessageAudits;
    }

    public SMSServiceResponse send(SMSServiceRequest smsServiceRequest) {

        String subscriberNumber = smsServiceRequest.getMobileNumber();
        String message = smsServiceRequest.getMessage();
        smsProviderService.send(subscriberNumber, message);

        log.info("Subscriber: " + subscriberNumber + ":" + message + " : @" + now());

        SMSAudit audit = new SMSAudit(subscriberNumber, smsServiceRequest.programName(), DateUtil.now(), message);
        this.allProgramMessageAudits.add(audit);
        return new SMSServiceResponse();
    }
}
