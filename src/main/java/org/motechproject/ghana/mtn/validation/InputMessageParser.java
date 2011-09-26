package org.motechproject.ghana.mtn.validation;

import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.exception.MessageParsingFailedException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InputMessageParser {

    public static final String ENROLLMENT_EXPRESSION = "^([Pp|Cc])\\s([\\d]{2})$";

    public Subscription parseMessage(String inputText) {
        Pattern pattern = Pattern.compile(ENROLLMENT_EXPRESSION);
        Matcher matcher = pattern.matcher(inputText);
        if (matcher.find()) {
            String campaignType = matcher.group(1);
            String startFrom = matcher.group(2);
            return new Subscription(campaignType, startFrom);
        }
        throw new MessageParsingFailedException("Input Message is not valid");
    }
}
