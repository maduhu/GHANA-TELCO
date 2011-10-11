package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.utils.InputMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionParser extends BaseSubscriptionProcess {
    private InputMessageParser inputMessageParser;

    @Autowired
    public SubscriptionParser(InputMessageParser inputMessageParser, SMSService smsService, MessageBundle messageBundle) {
        super(smsService, messageBundle);
        this.inputMessageParser = inputMessageParser;
    }

    public Subscription process(String mobileNumber, String input) {
        try {
            return inputMessageParser.parse(input);
        } catch (Exception e) {
            sendMessage(mobileNumber, messageFor(MessageBundle.ENROLLMENT_FAILURE));
        }
        return null;
    }
}
