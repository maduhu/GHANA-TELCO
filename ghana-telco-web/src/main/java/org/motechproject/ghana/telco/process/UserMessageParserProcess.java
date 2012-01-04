package org.motechproject.ghana.telco.process;

import org.motechproject.ghana.telco.domain.MessageBundle;
import org.motechproject.ghana.telco.domain.SMS;
import org.motechproject.ghana.telco.exception.InvalidMobileNumberException;
import org.motechproject.ghana.telco.parser.CompositeInputMessageParser;
import org.motechproject.ghana.telco.parser.RelativeProgramMessageParser;
import org.motechproject.ghana.telco.service.SMSService;
import org.motechproject.ghana.telco.vo.ParsedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMessageParserProcess extends BaseSubscriptionProcess {
    private CompositeInputMessageParser inputMessageParser;
    private RelativeProgramMessageParser relativeProgramMessageParser;

    @Autowired
    public UserMessageParserProcess(CompositeInputMessageParser inputMessageParser,
                                    RelativeProgramMessageParser relativeProgramMessageParser,
                                    SMSService smsService, MessageBundle messageBundle) {
        super(smsService, messageBundle);
        this.inputMessageParser = inputMessageParser;
        this.relativeProgramMessageParser = relativeProgramMessageParser;
    }

    public SMS process(String senderMobileNumber, String input) {
        try {
            ParsedRequest parsedRequest = relativeProgramMessageParser.parse(input, senderMobileNumber);
            if (null != parsedRequest)
                return inputMessageParser.parse(parsedRequest.getInputMessage(), parsedRequest.getSubscriberNumber()).setReferrer(senderMobileNumber);
            else
                return inputMessageParser.parse(input, senderMobileNumber);
        } catch (InvalidMobileNumberException exception) {
            sendMessage(senderMobileNumber, messageFor(MessageBundle.INVALID_MOBILE_NUMBER));
        } catch (Exception e) {
            sendMessage(senderMobileNumber, messageFor(MessageBundle.REQUEST_FAILURE));
        }
        return null;
    }
}
