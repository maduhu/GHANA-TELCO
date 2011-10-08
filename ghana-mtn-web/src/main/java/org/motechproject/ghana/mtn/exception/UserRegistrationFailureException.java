package org.motechproject.ghana.mtn.exception;

public class UserRegistrationFailureException extends RuntimeException {
    public UserRegistrationFailureException(String messageToSendToUser) {
        super(messageToSendToUser);
    }
}
