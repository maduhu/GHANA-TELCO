package org.motechproject.ghana.mtn.service.parser;

import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.SMS;
import org.motechproject.ghana.mtn.domain.StopSMS;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class StopMessageParser extends MessageParser {

    public static final String START_OF_PATTERN = "^((?i)stop)\\s(";    
    public static final String END_OF_PATTERN = ")?$";

    @Autowired
    public StopMessageParser(AllProgramTypes allProgramTypes) {
        super(allProgramTypes);
    }

    public SMS<IProgramType> parse(String input) {
        Matcher matcher = pattern().matcher(input);
        if (matcher.find()) {
            String program = matcher.group(2);
            ProgramType programType = program != null ? allProgramTypes.findByCampaignShortCode(program): null;
            return new StopSMS(input, programType);
        }
        return null;
    }

    public void recompilePatterns() {
        pattern = Pattern.compile(START_OF_PATTERN + getProgramCodePatterns() + END_OF_PATTERN, CASE_INSENSITIVE);
    }
}