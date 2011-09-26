package org.motechproject.ghana.mtn.domain;

public enum SubscriptionType {
    PREGNANCY("P"), CHILDCARE("C");
    private String matchingString;

    SubscriptionType(String matchingString) {
        this.matchingString = matchingString;
    }


    public static SubscriptionType fromString(String campaignType) {
        for (SubscriptionType type: values()) {
            if (type.matchingString.equalsIgnoreCase(campaignType))
                return type;
        }
        return null;
    }
}
