package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.exception.InvalidMobileNumberException;
import org.motechproject.ghana.mtn.parser.RelativeProgramMessageHandler;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.parser.InputMessageParser;
import org.motechproject.ghana.mtn.vo.ParsedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionUserMessageParser extends BaseSubscriptionProcess {
    private InputMessageParser inputMessageParser;
    private RelativeProgramMessageHandler relativeProgramMessageHandler;

    @Autowired
    public SubscriptionUserMessageParser(InputMessageParser inputMessageParser,
                                         RelativeProgramMessageHandler relativeProgramMessageParser,
                                         SMSService smsService, MessageBundle messageBundle) {
        super(smsService, messageBundle);
        this.inputMessageParser = inputMessageParser;
        this.relativeProgramMessageHandler = relativeProgramMessageParser;
    }

    public SMS process(String senderMobileNumber, String input) {
        try {
            ParsedRequest parsedRequest = relativeProgramMessageHandler.parse(input, senderMobileNumber);
            if (null != parsedRequest)
                return inputMessageParser.parse(parsedRequest.getInputMessage(), parsedRequest.getSubscriberNumber());
            else
                return inputMessageParser.parse(input, senderMobileNumber);
        } catch (InvalidMobileNumberException exception) {
            sendMessage(senderMobileNumber, messageFor(MessageBundle.INVALID_MOBILE_NUMBER));
        } catch (Exception e) {
            sendMessage(senderMobileNumber, messageFor(MessageBundle.ENROLLMENT_FAILURE));
        }
        return null;
    }
}
