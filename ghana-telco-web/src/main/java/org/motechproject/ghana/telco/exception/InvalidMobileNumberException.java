package org.motechproject.ghana.telco.exception;

import org.motechproject.ghana.telco.domain.MessageBundle;

public class InvalidMobileNumberException extends RuntimeException {
    public InvalidMobileNumberException() {
        super(MessageBundle.INVALID_MOBILE_NUMBER);
    }
}