package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;

public interface SubscriptionService {
    String enroll(SubscriptionRequest subscriptionRequest);
}
