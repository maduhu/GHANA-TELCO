package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.domain.Message;
import org.motechproject.ghana.mtn.repository.AllMessages;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.ghana.mtn.domain.MessageBundle.*;

@Component
public class MessageSeed extends Seed {
    @Autowired
    private AllMessages allMessages;

    @Override
    protected void load() {

        save(INVALID_MOBILE_NUMBER, "Invalid Phone Number");
        save(NOT_ENROLLED, "You are not subscribed to this program.");
        addEnrolment();
        addBilling();
        addStop();
        addRollover();
        addValidationError();
    }

    private void addEnrolment() {
        save(REQUEST_FAILURE, "Sorry we are having trouble processing your request.");
        save(ENROLLMENT_SUCCESS, "Welcome to Mobile Midwife ${p} Program. You are now enrolled & will receive SMSs full of great info every Mon,Weds &Fri. To stop these messages send STOP.");
        save(ENROLLMENT_STOPPED, "Your Mobile Midwife ${p} Program has ended. Thanks for using the program.");
        save(ENROLLMENT_ROLLOVER, "You have successfully been rolled over to the Mobile Midwife ${p} program.");
        save(ACTIVE_SUBSCRIPTION_PRESENT, "You already have an active ${p} Program Subscription. Please wait for the program to complete, or stop it to start a new one.");
    }

    private void addBilling() {
        save(BILLING_SUCCESS, "Your account has been charged with %s amount for the Mobile Midwife ${p} Service. Thank You for continuing to use the service.");
        save(BILLING_FAILURE, "Sorry we are having trouble processing your request.");
        save(BILLING_STOPPED, "Your account will be no longer charged for Mobile Midwife ${p} Program.");
        save(BILLING_ROLLOVER, "Your account will be now charged for the Mobile Midwife ${p} Program.");
    }

    private void addStop() {
        save(STOP_SPECIFY_PROGRAM, "Sorry we are having trouble processing your request. Please specify your enrolled program with your stop request.");
        save(STOP_SUCCESS, "Thank you for using the service. You can subscribe again at any time.");
        save(STOP_PROGRAM_SUCCESS, "Thank you for using the service. You can subscribe again at any time.");
    }

    private void addRollover() {
        save(ROLLOVER_NOT_POSSIBLE_PROGRAM_EXISTS_ALREADY, "We are unable to roll over your pregnancy program to child care program. You already have an active child care program. You can have only one active child care program at a time. Please message \"%s\" to xxxx to retain the existing program and terminate the roll over. " +
                "Please message \"%s\" to xxxx to continue with the roll over an terminate the existing program.");
        save(ROLLOVER_NO_PENDING_PREGNANCY_PROGRAM, "You do not have a pregnancy program pending roll over. We are unable to process your request.");

        save(PENDING_ROLLOVER_RETAIN_CHILDCARE, "Your pregnancy care program was terminated based on your input. Your existing child care program will continue to be active. Thanks for using the Mobile Midwife service.");
        save(PENDING_ROLLOVER_SWITCH_TO_NEW_CHILDCARE, "Your pregnancy care program was rolled over to child care program. Your existing child care program was terminated. Thanks for using the Mobile Midwife service.");
        save(PENDING_ROLLOVER_SWITCH_TO_NEW_CHILDCARE_BILLING, "Your account will be no longer charged for your previous Mobile Midwife Child Care Program. Your account will be now charged for the new Mobile Midwife Child Care Program instead of your pregnancy program.");
    }

    private void addValidationError() {
        save(ValidationError.INVALID_CUSTOMER.key(), "This service is for MTN Customers only. This is not a valid MTN Mobile Number.");
        save(ValidationError.INSUFFICIENT_FUNDS.key(), "There are no sufficient funds to proceed with the registration.");
    }

    private void save(String messageKey, String message) {
        allMessages.add(new Message(messageKey, message));
    }

}
