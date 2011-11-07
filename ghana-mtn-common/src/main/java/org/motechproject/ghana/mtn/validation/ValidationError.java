package org.motechproject.ghana.mtn.validation;

public enum ValidationError {
    INSUFFICIENT_FUNDS("validation.insufficient.funds"),
    INSUFFICIENT_FUNDS_DURING_REGISTRATION("registration.validation.insufficient.funds"),
    INVALID_CUSTOMER("validation.invalid.customer");

    private String key;

    ValidationError(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
