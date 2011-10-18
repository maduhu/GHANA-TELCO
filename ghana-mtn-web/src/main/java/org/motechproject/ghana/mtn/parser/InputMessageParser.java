package org.motechproject.ghana.mtn.parser;

import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;

@Component
public class InputMessageParser {

    private List<MessageParser> messageParsers;

    @Autowired
    public InputMessageParser(RegisterProgramMessageParser registerProgramParser,
                              StopMessageParser stopMessageParser, DeliveryMessageParser deliveryMessageParser) {
        messageParsers = asList(registerProgramParser, stopMessageParser, deliveryMessageParser);
    }

    public SMS parse(String message, String senderMobileNumber) {
        for (MessageParser parser : messageParsers) {
            SMS sms = parser.parse(message, senderMobileNumber);
            if(sms != null) return sms;
        }
        throw new MessageParseFailException("Input Message is not valid <" + message + ">");
    }

    public void recompilePatterns() {
        for (MessageParser parser : messageParsers) parser.recompilePatterns();
    }
}
