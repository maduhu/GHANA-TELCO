package org.motechproject.ghana.mtn.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'Subscriber'")
public class Subscriber extends MotechAuditableDataObject {
    private String subscriberNumber;

    public Subscriber() {
    }

    public Subscriber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscriber that = (Subscriber) o;

        if (subscriberNumber != null ? !subscriberNumber.equals(that.subscriberNumber) : that.subscriberNumber != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return subscriberNumber != null ? subscriberNumber.hashCode() : 0;
    }
}
