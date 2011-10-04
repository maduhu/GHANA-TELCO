package org.motechproject.ghana.mtn.domain.dto;

import org.motechproject.ghana.mtn.domain.SubscriptionType;

import java.util.List;

public class AdminRequest {

    private List<SubscriptionType> subscriptionTypes;

    public List<SubscriptionType> getSubscriptionTypes() {
        return subscriptionTypes;
    }

    public void setSubscriptionTypes(List<SubscriptionType> subscriptionTypes) {
        this.subscriptionTypes = subscriptionTypes;
    }
}
