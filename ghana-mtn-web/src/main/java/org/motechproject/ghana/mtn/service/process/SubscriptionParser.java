package org.motechproject.ghana.mtn.service.process;

import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.service.InputMessageParser;
import org.motechproject.ghana.mtn.service.sms.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionParser {
    private InputMessageParser inputMessageParser;
    private SMSService smsService;
    private MessageBundle messageBundle;

    @Autowired
    public SubscriptionParser(InputMessageParser inputMessageParser, SMSService smsService) {
        this.inputMessageParser = inputMessageParser;
        this.smsService = smsService;
    }

    public Subscription parseForEnrollment(String mobileNumber, String input) {
        try {
            return inputMessageParser.parse(input);
        } catch (Exception e) {
            sendSMS(mobileNumber);
        }
        return null;
    }

    public Subscription parseForWithDraw(String mobileNumber, String input) {
        try {
            return inputMessageParser.parse(input);
        } catch (Exception e) {
            sendSMS(mobileNumber);
        }
        return null;
    }

    private void sendSMS(String mobileNumber) {
        SMSServiceRequest request = new SMSServiceRequest(mobileNumber, messageBundle.get(MessageBundle.ENROLLMENT_FAILURE));
        smsService.send(request);
    }
}
