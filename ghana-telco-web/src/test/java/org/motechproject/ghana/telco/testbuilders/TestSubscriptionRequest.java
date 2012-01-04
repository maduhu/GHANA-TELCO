package org.motechproject.ghana.telco.testbuilders;

import org.motechproject.ghana.telco.domain.dto.SubscriptionRequest;

public class TestSubscriptionRequest {

    public static SubscriptionRequest with(String subscriberNumber, String inputMessage) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setSubscriberNumber(subscriberNumber);
        subscriptionRequest.setInputMessage(inputMessage);
        return subscriptionRequest;
    }

}
