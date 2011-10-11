package org.motechproject.ghana.mtn.domain.dto;

public class SubscriptionServiceRequest {
    private String subscriberNumber;
    private String inputMessage;

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public SubscriptionServiceRequest setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
        return this;
    }

    public String getInputMessage() {
        return inputMessage;
    }

    public SubscriptionServiceRequest setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
        return this;
    }
}
