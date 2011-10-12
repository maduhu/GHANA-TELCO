package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.Subscription;

public interface SubscriptionService {
    void start(Subscription subscription);
    void stop(Subscription subscription);
    Subscription findBy(String subscriberNumber, String programName);
}
