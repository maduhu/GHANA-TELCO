package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.Subscription;

public interface SubscriptionService {
    void start(Subscription subscriptionRequest);
    void stop(Subscription subscriptionRequest);
}
