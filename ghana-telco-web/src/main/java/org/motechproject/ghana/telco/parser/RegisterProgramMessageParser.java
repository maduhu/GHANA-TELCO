package org.motechproject.ghana.telco.parser;

import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.RegisterProgramSMS;
import org.motechproject.ghana.telco.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.telco.domain.vo.Week;
import org.motechproject.ghana.telco.domain.vo.WeekAndDay;
import org.motechproject.ghana.telco.exception.InvalidMonthException;
import org.motechproject.ghana.telco.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
public class RegisterProgramMessageParser extends MessageParser {

    public static final String START_OF_PATTERN = "^(";
    public static final String END_OF_PATTERN = ")\\s([\\d]{1,2})$";

    public RegisterProgramSMS parse(String input, String enrolledMobileNumber) {
        Matcher matcher = pattern().matcher(input);
        if (matcher.find()) {
            ProgramType programType = allProgramTypes.findByCampaignShortCode(matcher.group(1));
            RegisterProgramSMS registerProgramSMS = null;

            try {
                registerProgramSMS = new RegisterProgramSMS(input, new SubscriptionBuilder()
                        .withType(programType)
                        .withStartWeekAndDay(new WeekAndDay(new Week(programType.weekFor(Integer.parseInt(matcher.group(2)))), new DateUtils().today()))
                        .withRegistrationDate(new DateUtils().now())
                        .build());
            } catch (InvalidMonthException e) {
                return null;
            }

            registerProgramSMS.setFromMobileNumber(enrolledMobileNumber);
            return registerProgramSMS;
        }
        return null;
    }

    public void recompilePatterns() {
        pattern = Pattern.compile(START_OF_PATTERN + getProgramCodePatterns() + END_OF_PATTERN, CASE_INSENSITIVE);
    }
}
