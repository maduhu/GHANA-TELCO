package org.motechproject.ghana.telco.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechAuditableDataObject;

import static org.motechproject.util.DateUtil.setTimeZone;

@TypeDiscriminator("doc.type === 'SMSAudit'")
public class SMSAudit extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "SMSAudit";
    private String subscriberNumber;
    private String programKey;
    private DateTime sentTime;
    private String content;
    private String deliveryTime;

    public SMSAudit() {
    }

    public SMSAudit(String subscriberNumber, String programKey, DateTime sentTime, String content, String deliveryTime) {
        this.subscriberNumber = subscriberNumber;
        this.programKey = programKey;
        this.sentTime = sentTime;
        this.content = content;
        this.deliveryTime = deliveryTime;
    }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }

    public String getProgramKey() {
        return programKey;
    }

    public void setProgramKey(String programKey) {
        this.programKey = programKey;
    }

    public DateTime getSentTime() {
        return setTimeZone(sentTime);
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

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
