package org.motechproject.ghana.mtn.validation;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.motechproject.ghana.mtn.repository.AllSubscriptionTypes;
import org.motechproject.ghana.mtn.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InputMessageParser {
    public static final String ENROLLMENT_EXPRESSION = "^([Pp|Cc])\\s([\\d]{1,2})$";

    private AllSubscriptionTypes allSubscriptionTypes;

    @Autowired
    public InputMessageParser(AllSubscriptionTypes allSubscriptionTypes) {
        this.allSubscriptionTypes = allSubscriptionTypes;
    }

    public Subscription parse(String input) {
        Pattern pattern = Pattern.compile(ENROLLMENT_EXPRESSION);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return new SubscriptionBuilder()
                    .withType(allSubscriptionTypes.findByCampaignShortCode(matcher.group(1)))
                    .withStatus(SubscriptionStatus.ACTIVE)
                    .withStartWeekAndDay(new WeekAndDay(new Week(Integer.parseInt(matcher.group(2))), new DateUtils().today()))
                    .withRegistrationDate(DateTime.now())
                    .build();
        }
        throw new MessageParseFailException("Input Message is not valid <" + input + ">");
    }
}
