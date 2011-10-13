package org.motechproject.ghana.mtn.service.parser;

import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component("RelativeRegisterProgramMessageParser")
public class RelativeRegisterProgramMessageParser extends RegisterProgramMessageParser {

    public static final String START_OF_PATTERN = "^(R|Rel";
    public static final String END_OF_PATTERN = ")\\s([\\d]{10})\\s(.*)$";
    public static final int MOBILE_INDEX = 2;
    public static final int INPUT_MESSAGE_INDEX = 3;

    @Autowired
    public RelativeRegisterProgramMessageParser(AllProgramTypes allProgramTypes) {
        super(allProgramTypes);
    }

    public SMS<Subscription> parse(String input, String enrolledMobileNumber) {
        Matcher matcher = pattern().matcher(input);
        if (matcher.find()) {
            String mobileNumber = matcher.group(MOBILE_INDEX);

            String inputMessage = matcher.group(INPUT_MESSAGE_INDEX);

            return super.parse(inputMessage, mobileNumber);
        }
        return null;
    }

    public void recompilePatterns() {
        pattern = Pattern.compile(START_OF_PATTERN + END_OF_PATTERN, CASE_INSENSITIVE);
    }
}
