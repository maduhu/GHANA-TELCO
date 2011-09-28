package org.motechproject.ghana.mtn.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.model.MotechAuditableDataObject;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

@TypeDiscriminator("doc.type === 'Subscription'")
public class Subscription extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "Subscription";
    private Subscriber subscriber;
    private SubscriptionType subscriptionType;
    private SubscriptionStatus status;
    private Week startWeek;
    private DateTime registrationDate;

    public Subscription() {
    }

    @JsonIgnore
    public boolean isValid() {
        return subscriptionType.isInRange(startWeek.getNumber());
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public Week getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(Week startWeek) {
        this.startWeek = startWeek;
    }

    public DateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(DateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public CampaignRequest createCampaignRequest() {
        return new CampaignRequest(subscriber.getNumber(), subscriptionType.name(), null, null);
    }
}
