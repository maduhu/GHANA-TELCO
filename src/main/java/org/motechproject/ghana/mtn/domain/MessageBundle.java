package org.motechproject.ghana.mtn.domain;

//TODO - move to bundle properties
public interface MessageBundle {
    String SUCCESSFUL_ENROLLMENT_MESSAGE_FORMAT =
            "Welcome to Mobile Midwife %s Program. You are now enrolled & will receive SMSs full of great info every Mon,Weds &Fri." +
                    "To stop these messages send STOP";
    String FAILURE_ENROLLMENT_MESSAGE = "Sorry we are having trouble processing your request.";

}
