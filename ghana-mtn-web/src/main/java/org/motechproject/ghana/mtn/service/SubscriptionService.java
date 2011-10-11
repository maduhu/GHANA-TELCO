package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionServiceRequest;

public interface SubscriptionService {
    void startFor(SubscriptionServiceRequest subscriptionRequest);
    void endFor(SubscriptionServiceRequest subscriptionRequest);
}
