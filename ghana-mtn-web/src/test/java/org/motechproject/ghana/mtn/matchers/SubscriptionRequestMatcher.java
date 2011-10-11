package org.motechproject.ghana.mtn.matchers;

import org.mockito.ArgumentMatcher;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionServiceRequest;

public class SubscriptionRequestMatcher extends ArgumentMatcher<SubscriptionServiceRequest> {
    private String subscriberNumber;
    private String inputMessage;

    public SubscriptionRequestMatcher(String subscriberNumber, String inputMessage) {
        this.subscriberNumber = subscriberNumber;
        this.inputMessage = inputMessage;
    }

    @Override
    public boolean matches(Object o) {
        SubscriptionServiceRequest request = (SubscriptionServiceRequest) o;
        return request.getInputMessage().equals(inputMessage) && request.getSubscriberNumber().equals(subscriberNumber);
    }
}
