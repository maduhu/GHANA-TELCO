package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.service.parser.InputMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMessageParserHandle extends BaseSubscriptionProcess {
    private InputMessageParser inputMessageParser;

    @Autowired
    public UserMessageParserHandle(InputMessageParser inputMessageParser, SMSService smsService, MessageBundle messageBundle) {
        super(smsService, messageBundle);
        this.inputMessageParser = inputMessageParser;
    }

    public SMS process(String mobileNumber, String input) {
        try {
            return inputMessageParser.parse(input).setFromMobileNumber(mobileNumber);
        } catch (Exception e) {
            sendMessage(mobileNumber, messageFor(MessageBundle.ENROLLMENT_FAILURE));
        }
        return null;
    }
}
