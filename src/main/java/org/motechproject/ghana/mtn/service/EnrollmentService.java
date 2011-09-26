package org.motechproject.ghana.mtn.service;

public interface EnrollmentService {
    String SUCCESSFUL_ENROLLMENT_MESSAGE = "Welcome to Mobile Midwife Pregnancy Program." +
            " You are now enrolled & will receive SMSs full of great info every Mon,Weds &Fri." +
            " To stop these messages send STOP";

    String enrollSubscriber(String subscriberNumber, String inputMessage);
}
