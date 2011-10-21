package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;

import java.util.Date;
import java.util.List;

public interface SubscriptionService {
    void start(Subscription subscription);
    void stopExpired(Subscription subscription);    
    void rollOver(String fromMobileNumber, Date deliveryDate);
    void rollOverByEvent(Subscription subscription);
    void stopByUser(String fromMobileNumber, IProgramType domain);
    void retainOrRollOver(String subscriberNumber, boolean retainSubscription);
    Subscription findActiveSubscriptionFor(String subscriberNumber, String programName);
    List<Subscription> activeSubscriptions(String subscriberNumber);
}
