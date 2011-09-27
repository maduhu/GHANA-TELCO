package org.motechproject.ghana.mtn.domain;

import org.apache.commons.lang.math.IntRange;

public enum SubscriptionType {
    PREGNANCY("P", "Pregnancy", new IntRange(5, 35)),
    CHILDCARE("C", "Child Care", new IntRange(1, 52));

    private String matchingString;
    private IntRange weekRange;
    private String programName;

    SubscriptionType(String matchingString, String programName, IntRange weekRange) {
        this.matchingString = matchingString;
        this.programName = programName;
        this.weekRange = weekRange;
    }


    public static SubscriptionType of(String campaignType) {
        for (SubscriptionType type: values()) {
            if (type.matchingString.equalsIgnoreCase(campaignType))
                return type;
        }
        return null;
    }

    public boolean isInRange(Integer startFrom) {
        return weekRange.containsInteger(startFrom);
    }

    public String getProgramName() {
        return programName;
    }
}
