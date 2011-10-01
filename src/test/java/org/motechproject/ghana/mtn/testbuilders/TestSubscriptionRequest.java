package org.motechproject.ghana.mtn.testbuilders;

import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;

public class TestSubscriptionRequest {

    public static SubscriptionRequest with(String subscriberNumber, String inputMessage) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setSubscriberNumber(subscriberNumber);
        subscriptionRequest.setInputMessage(inputMessage);
        return subscriptionRequest;
    }

}
