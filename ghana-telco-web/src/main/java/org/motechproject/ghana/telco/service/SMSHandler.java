package org.motechproject.ghana.telco.service;

import org.motechproject.ghana.telco.domain.*;
import org.motechproject.ghana.telco.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.telco.exception.InvalidProgramException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMSHandler {

    SubscriptionService service;
    private SMSService smsService;

    @Autowired
    public SMSHandler(SubscriptionService service, SMSService smsService) {
        this.service = service;
        this.smsService = smsService;
    }

    public void register(RegisterProgramSMS sms) {
        Subscription subscription = sms.getDomain();
        Subscriber subscriber = new Subscriber(sms.getFromMobileNumber());
        subscription.setSubscriber(subscriber);
        service.start(subscription);
    }

    public void stop(StopSMS sms) {
        service.stopByUser(sms.getFromMobileNumber(), sms.getDomain());
    }

    public void rollOver(DeliverySMS deliverySMS) {
        service.rollOver(deliverySMS.getFromMobileNumber(), deliverySMS.getDomain());
    }

    public void retainOrRollOverChildCare(RetainOrRollOverChildCareProgramSMS retainOrRollOverChildCareProgramSMS) {
        String subscriberNumber = retainOrRollOverChildCareProgramSMS.getFromMobileNumber();
        try {
            service.retainOrRollOver(subscriberNumber, retainOrRollOverChildCareProgramSMS.retainExistingChildCareProgram());
        } catch( InvalidProgramException ipe) {
            smsService.send(new SMSServiceRequest(subscriberNumber, ipe.getMessage()));
        }
    }
}
