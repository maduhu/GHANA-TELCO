package org.motechproject.ghana.mtn.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.utils.DateUtils;
import org.motechproject.model.MotechAuditableDataObject;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

@TypeDiscriminator("doc.type === 'Subscription'")
public class Subscription extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "Subscription";

    private Subscriber subscriber;
    private SubscriptionType subscriptionType;
    private SubscriptionStatus status;

    private WeekAndDay startWeekAndDay;
    private WeekAndDay lastMsgSentWeekAndDay;

    private DateTime registrationDate;
    private DateUtils dateUtils = new DateUtils();

    public Subscription() {
    }

    @JsonIgnore
    public boolean isNotValid() {
        return !subscriptionType.isInRange(startWeekAndDay.getWeek().getNumber());
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

    public WeekAndDay getStartWeekAndDay() {
        return startWeekAndDay;
    }

    public void setStartWeekAndDay(WeekAndDay startWeekAndDay) {
        this.startWeekAndDay = startWeekAndDay;
    }

    public DateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(DateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public CampaignRequest createCampaignRequest() {
        return new CampaignRequest(subscriber.getNumber(), subscriptionType.getProgramName(), null, null);
    }

    public Week currentWeek() {
        Period period = new Period(registrationDate, dateUtils.now(), PeriodType.weeks());
        return startWeekAndDay.getWeek().add(period.getWeeks());
    }

    public Day currentDay() {
        String day = dateUtils.now().dayOfWeek().getAsText();
        return Day.valueOf(day.toUpperCase());
    }

    public String programName() {
        return subscriptionType.getProgramName();
    }

    public void updateLastMessageSent() {
        lastMsgSentWeekAndDay = new WeekAndDay(currentWeek(), currentDay());
    }

    public WeekAndDay getLastMsgSentWeekAndDay() {
        return lastMsgSentWeekAndDay;
    }

    public void setLastMsgSentWeekAndDay(WeekAndDay lastMsgSentWeekAndDay) {
        this.lastMsgSentWeekAndDay = lastMsgSentWeekAndDay;
    }

    public boolean alreadySent(SubscriptionMessage subscriptionMessage) {
        return lastMsgSentWeekAndDay != null && subscriptionMessage.getWeekAndDay().isBefore(lastMsgSentWeekAndDay);
    }
}
