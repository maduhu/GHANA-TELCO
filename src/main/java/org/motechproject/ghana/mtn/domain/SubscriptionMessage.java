package org.motechproject.ghana.mtn.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'SubscriptionType'")
public class SubscriptionMessage extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "SubscriptionMessage";
    private String subscriptionId;
    private String content;
    private Week week;
    private Day day;

    public SubscriptionMessage() {
    }

    public SubscriptionMessage(String subscriptionId, String content, Week week, Day day) {
        this.subscriptionId = subscriptionId;
        this.content = content;
        this.week = week;
        this.day = day;
    }

    public String getSubscriptionId() {
       return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }
}