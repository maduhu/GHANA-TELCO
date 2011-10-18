package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;

import java.util.Date;
import java.util.List;

public interface SubscriptionService {
    void start(Subscription subscription);
    void stopExpired(Subscription subscription);
    void rollOver(Subscription fromSubscription, Subscription toSubscription);
    void rollOver(String fromMobileNumber, Date deliveryDate);
    void stopByUser(String fromMobileNumber, IProgramType domain);
    void rollOverByEvent(Subscription subscription);
    Subscription findBy(String subscriberNumber, String programName);
    List<Subscription> activeSubscriptions(String subscriberNumber);
}
