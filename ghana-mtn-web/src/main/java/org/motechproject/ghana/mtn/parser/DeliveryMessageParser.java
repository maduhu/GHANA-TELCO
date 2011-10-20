package org.motechproject.ghana.mtn.parser;

import org.motechproject.ghana.mtn.domain.DeliverySMS;
import org.motechproject.util.DateUtil;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.motechproject.ghana.mtn.domain.ShortCode.DELIVERY;

@Component
public class DeliveryMessageParser extends MessageParser {

    public static final String DELIVERY_MESSAGE = "^(%s)\\s?(\\s([\\d]{1,2}-[\\d]{1,2}))?$";

    public DeliverySMS parse(String input, String senderMobileNumber) {
        Matcher matcher = pattern().matcher(input.trim());
        if (matcher.find()) {
            DeliverySMS sms = new DeliverySMS(input, DateUtil.today().toDate());
            sms.setFromMobileNumber(senderMobileNumber);
            return sms;
        }
        return null;
    }

    public void recompilePatterns() {
        pattern = Pattern.compile(format(DELIVERY_MESSAGE, shortCodePattern(DELIVERY)), CASE_INSENSITIVE);
    }
}

