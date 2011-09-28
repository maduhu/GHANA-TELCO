package org.motechproject.ghana.mtn.validation;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.exception.MessageParseFailException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InputMessageParser {

    public static final String ENROLLMENT_EXPRESSION = "^([Pp|Cc])\\s([5-9]|[\\d]{2})$";

    public Subscription parse(String input) {
        Pattern pattern = Pattern.compile(ENROLLMENT_EXPRESSION);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            SubscriptionBuilder builder = new SubscriptionBuilder();
            return builder
                    .withType(SubscriptionType.of(matcher.group(1)))
                    .withStatus(SubscriptionStatus.ACTIVE)
                    .withStartWeek(new Week(Integer.parseInt(matcher.group(2))))
                    .withRegistrationDate(DateTime.now())
                    .create();
        }
        throw new MessageParseFailException("Input Message is not valid <" + input + ">");
    }
}
