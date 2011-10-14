package org.motechproject.ghana.mtn.parser;

import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.motechproject.ghana.mtn.domain.ShortCode.STOP;


@Component
public class StopMessageParser extends MessageParser {

    public static final String STOP_PATTERN = "^(%s)\\s?(\\s([%s]))?$";

    @Autowired
    public StopMessageParser(AllProgramTypes allProgramTypes) {
        super(allProgramTypes);
    }

    public SMS<IProgramType> parse(String input, String enrolledMobileNumber) {
        Matcher matcher = pattern().matcher(input.trim());
        if (matcher.find()) {
            String program = matcher.group(3);
            ProgramType programType = program != null ? allProgramTypes.findByCampaignShortCode(program): null;
            StopSMS stopSMS = new StopSMS(input, programType);
            stopSMS.setFromMobileNumber(enrolledMobileNumber);
            return stopSMS;
        }
        return null;
    }

    public void recompilePatterns() {
        pattern = Pattern.compile(format(STOP_PATTERN , shortCodePattern(STOP), getProgramCodePatterns()), CASE_INSENSITIVE);
    }
}
