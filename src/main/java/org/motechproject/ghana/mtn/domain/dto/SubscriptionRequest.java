package org.motechproject.ghana.mtn.domain.dto;

public class SubscriptionRequest {
    private String subscriberNumber;
    private String inputMessage;

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

    public String getInputMessage() {
        return inputMessage;
    }

    public void setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
    }
}
