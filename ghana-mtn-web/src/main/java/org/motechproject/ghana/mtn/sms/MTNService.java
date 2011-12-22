package org.motechproject.ghana.mtn.sms;

import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.util.DateUtil;
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
    public boolean send(String mobileNumber, String payload, Time deliveryTime) {
        if (null != deliveryTime) {
            DateTime now = DateUtil.now();
            DateTime deliveryDateTime = now.withTimeAtStartOfDay().withHourOfDay(deliveryTime.getHour()).withMinuteOfHour(deliveryTime.getMinute());
            if (deliveryDateTime.isBefore(now)) {
                deliveryDateTime = deliveryDateTime.plusDays(1);
            }
            smsService.sendSMS(mobileNumber, payload, deliveryDateTime);
        }
        smsService.sendSMS(mobileNumber, payload);
        return true;
    }
}
