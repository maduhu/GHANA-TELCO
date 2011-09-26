package org.motechproject.ghana.mtn.domain;

public class Subscription {
    private SubscriptionType type;
    private Integer startFrom;

    public Subscription(String campaignType, String startFrom) {
        this.type = SubscriptionType.fromString(campaignType);
        this.startFrom = Integer.parseInt(startFrom);
    }

    public SubscriptionType getType() {
        return type;
    }

    public Integer getStartFrom() {
        return startFrom;
    }
}
