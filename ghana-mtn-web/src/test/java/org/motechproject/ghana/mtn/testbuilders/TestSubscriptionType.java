package org.motechproject.ghana.mtn.testbuilders;

import org.motechproject.ghana.mtn.domain.SubscriptionType;

import java.util.List;

public class TestSubscriptionType {

     public static SubscriptionType with(String programName, Integer minWeek, Integer maxWeek, List<String> shortCodes){
        SubscriptionType subscriptionType = new SubscriptionType();
        subscriptionType.setMaxWeek(maxWeek);
        subscriptionType.setMinWeek(minWeek);
        subscriptionType.setProgramName(programName);
        subscriptionType.setShortCodes(shortCodes);
        return subscriptionType;
    }
}
