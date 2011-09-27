package org.motechproject.ghana.mtn.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'Subscriber'")
public class Subscriber extends MotechAuditableDataObject {
    private String number;

    public Subscriber() {
    }

    public Subscriber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscriber that = (Subscriber) o;
        if (number != null ? !number.equals(that.number) : that.number != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return number != null ? number.hashCode() : 0;
    }
}
