package org.motechproject.ghana.mtn.domain;

import java.util.HashMap;
import java.util.Map;

//TODO - move to bundle properties
public abstract class MessageBundle {

    static Map<String, String> messageBundle = new HashMap<String, String>();
    public static final String SUCCESSFUL_ENROLLMENT_MESSAGE_FORMAT = "SUCCESSFUL_ENROLLMENT_MESSAGE_FORMAT";
    public static final String FAILURE_ENROLLMENT_MESSAGE = "FAILURE_ENROLLMENT_MESSAGE";
    public static final String ACTIVE_SUBSCRIPTION_ALREADY_PRESENT = "ACTIVE_SUBSCRIPTION_ALREADY_PRESENT";
    public static final String INSUFFICIENT_FUND = "INSUFFICIENT_FUND";
    public static final String NOT_A_VALID_CUSTOMER = "NOT_A_VALID_CUSTOMER";

    static {
        messageBundle.put(SUCCESSFUL_ENROLLMENT_MESSAGE_FORMAT,
            "Welcome to Mobile Midwife %s Program. You are now enrolled & will receive SMSs full of great info every Mon,Weds &Fri." +
                    "To stop these messages send STOP");
        messageBundle.put(FAILURE_ENROLLMENT_MESSAGE, "Sorry we are having trouble processing your request.");
        messageBundle.put(ACTIVE_SUBSCRIPTION_ALREADY_PRESENT, "You already have an active %s Program Subscription. Please wait for the program to complete, or stop it to start a new one");
        messageBundle.put(INSUFFICIENT_FUND, "There aren’t sufficient funds to proceed with the registration.");
        messageBundle.put(NOT_A_VALID_CUSTOMER, "This service is for MTN Customers only. This is not a valid MTN Mobile Number");
    }

    public static String getMessage(String key) {
        return messageBundle.get(key);
    }
}
