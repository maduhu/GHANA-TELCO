package org.motechproject.ghana.mtn.service.process;

import org.motechproject.ghana.mtn.domain.Subscription;

public interface ISubscriptionProcessFlow {
    Boolean startFor(Subscription subscription);
    Boolean stopFor(Subscription subscription);

}
