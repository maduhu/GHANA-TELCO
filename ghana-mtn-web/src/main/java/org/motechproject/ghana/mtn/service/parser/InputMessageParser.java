package org.motechproject.ghana.mtn.service.parser;

import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;

@Component
public class InputMessageParser {

    private RegisterProgramMessageParser registerProgramParser;
    private StopMessageParser stopMessageParser;
    private List<MessageParser> messageParsers;

    @Autowired
    public InputMessageParser(RegisterProgramMessageParser registerProgramParser, StopMessageParser stopMessageParser) {
        this.registerProgramParser = registerProgramParser;
        this.stopMessageParser = stopMessageParser;
        messageParsers = asList(this.registerProgramParser, this.stopMessageParser);
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
