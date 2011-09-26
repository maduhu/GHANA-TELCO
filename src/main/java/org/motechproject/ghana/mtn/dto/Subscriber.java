package org.motechproject.ghana.mtn.dto;

public class Subscriber {
    private String subscriberNumber;
    private String inputMessage;

    public Subscriber(String subscriberNumber, String inputMessage) {
        this.subscriberNumber = subscriberNumber;
        this.inputMessage = inputMessage;
    }
}
