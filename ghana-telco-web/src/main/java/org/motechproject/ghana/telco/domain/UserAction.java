package org.motechproject.ghana.telco.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'UserAction'")
public class UserAction extends MotechBaseDataObject {
    @JsonProperty
    private String userName;
    @JsonProperty
    private DateTime dateTime;
    @JsonProperty
    private String task;
    @JsonProperty
    private String subscriberNumber;

    public UserAction(String userName, DateTime dateTime, String task, String subscriberNumber) {
        this.userName = userName;
        this.dateTime = dateTime;
        this.task = task;
        this.subscriberNumber = subscriberNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }
}
