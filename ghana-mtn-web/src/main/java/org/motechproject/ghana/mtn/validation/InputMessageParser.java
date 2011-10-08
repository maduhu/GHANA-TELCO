package org.motechproject.ghana.mtn.validation;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.motechproject.ghana.mtn.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.*;

@Component
public class InputMessageParser {
    public static final String START_OF_PATTERN = "^(";
    private static String programCodes = "P|p|C|c";
    public static final String END_OF_PATTERN = ")\\s([\\d]{1,2})$";
    public static Pattern SUBSCRIBER_ENROLLMENT_PATTERN = Pattern.compile(START_OF_PATTERN + programCodes + END_OF_PATTERN);
    private AllProgramTypes allProgramTypes;

    @Autowired
    public InputMessageParser(AllProgramTypes allProgramTypes) {
        this.allProgramTypes = allProgramTypes;
    }

    public Subscription parse(String input) {
        Matcher matcher = SUBSCRIBER_ENROLLMENT_PATTERN.matcher(input.toUpperCase());
        if (matcher.find()) {
            return new SubscriptionBuilder()
                    .withType(allProgramTypes.findByCampaignShortCode(matcher.group(1)))
                    .withStartWeekAndDay(new WeekAndDay(new Week(Integer.parseInt(matcher.group(2))), new DateUtils().today()))
                    .withRegistrationDate(DateTime.now())
                    .build();
        }
        throw new MessageParseFailException("Input Message is not valid <" + input + ">");
    }

    public void recompilePattern() {
        programCodes = joinFrom(flatten(extract(allProgramTypes.getAll(), on(ProgramType.class).getShortCodes())), "|").toString();
        SUBSCRIBER_ENROLLMENT_PATTERN = Pattern.compile(START_OF_PATTERN + programCodes + END_OF_PATTERN);
    }
}
