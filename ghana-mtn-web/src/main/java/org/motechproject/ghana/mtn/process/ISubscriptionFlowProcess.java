package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.domain.Subscription;

public interface ISubscriptionFlowProcess {
    Boolean startFor(Subscription subscription);
    Boolean stopFor(Subscription subscription);
    Boolean rollOver(Subscription fromSubscription, Subscription toSubscription);

}
