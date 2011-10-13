package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.Subscription;

public interface SubscriptionService {
    void start(Subscription subscription);
    void stop(Subscription subscription);
    void rollOver(Subscription fromSubscription, Subscription toSubscription);
    Subscription findBy(String subscriberNumber, String programName);
}
