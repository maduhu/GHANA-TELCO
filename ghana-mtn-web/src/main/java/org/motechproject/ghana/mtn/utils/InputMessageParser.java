package org.motechproject.ghana.mtn.utils;

import org.motechproject.ghana.mtn.domain.MessageParser;
import org.motechproject.ghana.mtn.domain.RegisterProgramMessageParser;
import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;

@Component
public class InputMessageParser {

    private RegisterProgramMessageParser registerProgramParser;
    private List<? extends MessageParser> messageParsers;

    @Autowired
    public InputMessageParser(RegisterProgramMessageParser registerProgramParser) {
        this.registerProgramParser = registerProgramParser;
        messageParsers = asList(this.registerProgramParser);
    }

    public SMS parse(String message) {

        for(MessageParser parser : messageParsers) {
            SMS sms = parser.parse(message);
            if(sms != null) return sms;
        }
        throw new MessageParseFailException("Input Message is not valid <" + message + ">");
    }

    public void recompilePatterns() {
        for(MessageParser parser : messageParsers) parser.recompilePatterns();
    }
}
