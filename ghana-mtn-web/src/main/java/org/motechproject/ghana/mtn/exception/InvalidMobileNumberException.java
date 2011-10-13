package org.motechproject.ghana.mtn.exception;

import org.motechproject.ghana.mtn.domain.MessageBundle;

public class InvalidMobileNumberException extends RuntimeException {
    public InvalidMobileNumberException() {
        super(MessageBundle.INVALID_MOBILE_NUMBER);
    }
}