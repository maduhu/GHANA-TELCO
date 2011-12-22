package org.motechproject.ghana.mtn.sms;

import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MTNService implements SMSProvider {

    SmsService smsService;

    @Autowired
    public MTNService(SmsService smsService) {
        this.smsService = smsService;
    }

    @Override
    public boolean send(String mobileNumber, String payload) {
        smsService.sendSMS(mobileNumber,payload);
        return true;
    }
}
