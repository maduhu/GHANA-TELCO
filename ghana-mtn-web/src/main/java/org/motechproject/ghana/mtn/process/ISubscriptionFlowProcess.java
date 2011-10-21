package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.domain.Subscription;

public interface ISubscriptionFlowProcess {
    Boolean startFor(Subscription subscription);
    Boolean stopExpired(Subscription subscription);
    Boolean stopByUser(Subscription subscription);
    Boolean rollOver(Subscription fromSubscription, Subscription toSubscription);
    Boolean retainExistingChildCare(Subscription pregnancySubscriptionWaitingForRollOver, Subscription childCareSubscription);
}
