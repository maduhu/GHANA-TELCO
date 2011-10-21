package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMSHandler {

    SubscriptionService service;

    @Autowired
    public SMSHandler(SubscriptionService service) {
        this.service = service;
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
        service.retainOrRollOver(retainOrRollOverChildCareProgramSMS.getFromMobileNumber(), retainOrRollOverChildCareProgramSMS.getDomain());
    }
}
