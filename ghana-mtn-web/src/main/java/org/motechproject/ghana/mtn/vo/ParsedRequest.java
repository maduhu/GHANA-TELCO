package org.motechproject.ghana.mtn.vo;

public class ParsedRequest {
    private String subscriberNumber;

    private String senderNumber;

    private String inputMessage;
    public ParsedRequest(String subscriberNumber, String senderNumber, String inputMessage) {
        this.subscriberNumber = subscriberNumber;
        this.senderNumber = senderNumber;
        this.inputMessage = inputMessage;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public String getInputMessage() {
        return inputMessage;
    }
}
