package org.motechproject.ghana.mtn.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'Subscription'")
public class Subscription extends MotechAuditableDataObject {
    private SubscriptionType type;
    private Integer startFrom;
    private SubscriptionStatus subscriptionStatus;

    public Subscription() {
    }

    public Subscription(String campaignType, String startFrom) {
        this.type = SubscriptionType.fromString(campaignType);
        this.startFrom = Integer.parseInt(startFrom);
        this.subscriptionStatus = SubscriptionStatus.ACTIVE;
    }

    public SubscriptionType getType() {
        return type;
    }

    public Integer getStartFrom() {
        return startFrom;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        if (startFrom != null ? !startFrom.equals(that.startFrom) : that.startFrom != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (startFrom != null ? startFrom.hashCode() : 0);
        return result;
    }
}
