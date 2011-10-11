package org.motechproject.ghana.mtn.matchers;

import org.mockito.ArgumentMatcher;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;

public class SubscriptionRequestMatcher extends ArgumentMatcher<SubscriptionRequest> {
    private String subscriberNumber;
    private String inputMessage;

    public SubscriptionRequestMatcher(String subscriberNumber, String inputMessage) {
        this.subscriberNumber = subscriberNumber;
        this.inputMessage = inputMessage;
    }

    @Override
    public boolean matches(Object o) {
        SubscriptionRequest request = (SubscriptionRequest) o;
        return request.getInputMessage().equals(inputMessage) && request.getSubscriberNumber().equals(subscriberNumber);
    }
}
