package org.motechproject.ghana.mtn.domain.dto;

public class EnrollmentRequest {
    private String subscriberNumber;
    private String week;

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
