package org.motechproject.ghana.telco.exception;

public class UserRegistrationFailureException extends RuntimeException {
    public UserRegistrationFailureException(String messageToSendToUser) {
        super(messageToSendToUser);
    }
}
