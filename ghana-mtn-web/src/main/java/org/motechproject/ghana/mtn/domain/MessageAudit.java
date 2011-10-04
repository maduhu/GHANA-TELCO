package org.motechproject.ghana.mtn.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'MessageAudit'")
public class MessageAudit extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "MessageAudit";

    private String subscriberNumber;
    private String programName;
    private DateTime sentTime;
    private String content;

    public MessageAudit() {
    }

    public MessageAudit(String subscriberNumber, String programName, DateTime sentTime, String content) {
        this.subscriberNumber = subscriberNumber;
        this.programName = programName;
        this.sentTime = sentTime;
        this.content = content;
    }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public DateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(DateTime sentTime) {
        this.sentTime = sentTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
