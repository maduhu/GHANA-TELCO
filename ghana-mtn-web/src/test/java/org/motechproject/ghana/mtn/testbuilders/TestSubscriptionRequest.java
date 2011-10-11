package org.motechproject.ghana.mtn.testbuilders;

import org.motechproject.ghana.mtn.domain.dto.SubscriptionServiceRequest;

public class TestSubscriptionRequest {

    public static SubscriptionServiceRequest with(String subscriberNumber, String inputMessage) {
        SubscriptionServiceRequest subscriptionRequest = new SubscriptionServiceRequest();
        subscriptionRequest.setSubscriberNumber(subscriberNumber);
        subscriptionRequest.setInputMessage(inputMessage);
        return subscriptionRequest;
    }

}
