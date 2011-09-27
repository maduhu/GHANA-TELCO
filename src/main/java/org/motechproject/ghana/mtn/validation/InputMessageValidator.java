package org.motechproject.ghana.mtn.validation;

import org.motechproject.ghana.mtn.domain.Subscription;
import org.springframework.stereotype.Component;

@Component
public class InputMessageValidator {

    public boolean validate(Subscription subscription) {
        return subscription.getType().isInRange(subscription.getStartFrom());
    }
}
