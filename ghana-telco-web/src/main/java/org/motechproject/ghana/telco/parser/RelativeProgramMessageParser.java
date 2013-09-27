package org.motechproject.ghana.telco.parser;

import org.motechproject.ghana.telco.domain.ShortCode;
import org.motechproject.ghana.telco.exception.InvalidMobileNumberException;
import org.motechproject.ghana.telco.repository.AllShortCodes;
import org.motechproject.ghana.telco.vo.ParsedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.*;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
public class RelativeProgramMessageParser {

    private static final String START_OF_PATTERN = "^(";
    private static final String END_OF_PATTERN = ")\\s*([\\d]+)\\s(.*)$";
    private static final int MOBILE_INDEX = 2;
    private static final int INPUT_MESSAGE_INDEX = 3;
    private static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^0[\\d]{9}$");
    private Pattern pattern;
    private AllShortCodes allShortCodes;

    @Autowired
    public RelativeProgramMessageParser(AllShortCodes allShortCodes) {
        this.allShortCodes = allShortCodes;
    }

    public ParsedRequest parse(String input, String senderNumber) {
        Matcher matcher = getPattern().matcher(input);
        if (matcher.find()) {
            String mobileNumber = matcher.group(MOBILE_INDEX);
            if (!isValidMobileNumber(mobileNumber))
                throw new InvalidMobileNumberException();

            return new ParsedRequest(mobileNumber, senderNumber, matcher.group(INPUT_MESSAGE_INDEX));
        }
        return null;
    }

    private boolean isValidMobileNumber(String mobileNumber) {
        return MOBILE_NUMBER_PATTERN.matcher(mobileNumber).find();
    }

    public void recompilePatterns() {
        pattern = Pattern.compile(START_OF_PATTERN + getShortCodesForRelativeSubscription() + END_OF_PATTERN, CASE_INSENSITIVE);
    }

    private String getShortCodesForRelativeSubscription() {
        return joinFrom(flatten(extract(allShortCodes.getShortCodeFor(ShortCode.RELATIVE).getCodes(), on(String.class))), "|").toString();
    }

    public Pattern getPattern() {
        if (pattern == null)
            recompilePatterns();
        return pattern;
    }
}
