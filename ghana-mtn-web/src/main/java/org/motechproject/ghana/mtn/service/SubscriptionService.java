package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionServiceRequest;

public interface SubscriptionService {
    String enroll(SubscriptionServiceRequest subscriptionRequest);
    Subscription findBy(String subscriberNumber, String programName);
}
