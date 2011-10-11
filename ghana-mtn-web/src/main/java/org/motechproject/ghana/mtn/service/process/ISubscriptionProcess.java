package org.motechproject.ghana.mtn.service.process;

import org.motechproject.ghana.mtn.domain.Subscription;

public interface ISubscriptionProcess {
    Boolean startFor(Subscription subscription);
    Boolean endFor(Subscription subscription);

}
