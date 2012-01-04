package org.motechproject.ghana.telco.domain.dto;

public class SubscriptionRequest {
    private String subscriberNumber;
    private String inputMessage;

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public SubscriptionRequest setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
        return this;
    }

    public String getInputMessage() {
        return inputMessage;
    }

    public SubscriptionRequest setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
        return this;
    }
}
