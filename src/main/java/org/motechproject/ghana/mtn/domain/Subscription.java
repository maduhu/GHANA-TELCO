package org.motechproject.ghana.mtn.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.model.MotechAuditableDataObject;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

@TypeDiscriminator("doc.type === 'Subscription'")
public class Subscription extends MotechAuditableDataObject {
    private Subscriber subscriber;
    private SubscriptionType type;
    private SubscriptionStatus status;
    private Week startWeek;
    private DateTime registrationDate;

    public Subscription() {
    }

    public boolean isValid() {
        return type.isInRange(startWeek.number());
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public SubscriptionType getType() {
        return type;
    }

    public void setType(SubscriptionType type) {
        this.type = type;
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
        return new CampaignRequest(subscriber.getNumber(), type.name(), null, null);
    }
}
