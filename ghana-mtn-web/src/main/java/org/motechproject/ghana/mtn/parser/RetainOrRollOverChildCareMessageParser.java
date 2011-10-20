package org.motechproject.ghana.mtn.parser;

import org.motechproject.ghana.mtn.domain.RetainOrRollOverChildCareProgramSMS;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

import static java.lang.String.format;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.motechproject.ghana.mtn.domain.ShortCode.RETAIN_EXISTING_CHILDCARE_PROGRAM;
import static org.motechproject.ghana.mtn.domain.ShortCode.USE_ROLLOVER_TO_CHILDCARE_PROGRAM;


@Component
public class RetainOrRollOverChildCareMessageParser extends MessageParser {

    public static final String ROLLOVER_DECISION_PATTERN = "^((%s)\\z|(%s)\\z)$";

    public RetainOrRollOverChildCareProgramSMS parse(String input, String enrolledMobileNumber) {
        Matcher matcher = pattern().matcher(input.trim());
        if (matcher.find()) {
            String rolloverDecisionYes = matcher.group(2);
            RetainOrRollOverChildCareProgramSMS retainOrRollOverChildCareSMS = new RetainOrRollOverChildCareProgramSMS(input, isNotEmpty(rolloverDecisionYes));
            retainOrRollOverChildCareSMS.setFromMobileNumber(enrolledMobileNumber);
            return retainOrRollOverChildCareSMS;
        }
        return null;
    }

    public void recompilePatterns() {
        pattern = compile(format(ROLLOVER_DECISION_PATTERN, shortCodePattern(RETAIN_EXISTING_CHILDCARE_PROGRAM), shortCodePattern(USE_ROLLOVER_TO_CHILDCARE_PROGRAM)), CASE_INSENSITIVE);
    }
}
