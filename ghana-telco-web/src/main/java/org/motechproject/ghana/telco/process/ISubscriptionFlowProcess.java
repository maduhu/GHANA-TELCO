package org.motechproject.ghana.telco.process;

import org.motechproject.ghana.telco.domain.Subscription;

public interface ISubscriptionFlowProcess {
    Boolean startFor(Subscription subscription);

    Boolean stopExpired(Subscription subscription);

    Boolean stopByUser(Subscription subscription);

    Boolean rollOver(Subscription fromSubscription, Subscription toSubscription);

    Boolean retainExistingChildCare(Subscription pregnancySubscriptionWaitingForRollOver, Subscription childCareSubscription);

    Boolean rollOverToNewChildCareProgram(Subscription pregnancyProgramWaitingForRollOver, Subscription newChildCareToRollOver, Subscription existingChildCare);
}
