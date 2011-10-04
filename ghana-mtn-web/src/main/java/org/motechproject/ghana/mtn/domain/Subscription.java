package org.motechproject.ghana.mtn.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.*;
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
    private ProgramType programType;
    private SubscriptionStatus status;

    private WeekAndDay startWeekAndDay;
    private WeekAndDay lastMsgSentWeekAndDay;

    private DateTime registrationDate;
    private DateUtils dateUtils = new DateUtils();

    public Subscription() {
    }

    @JsonIgnore
    public boolean isNotValid() {
        return !programType.isInRange(startWeekAndDay.getWeek().getNumber());
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
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
        return new CampaignRequest(subscriber.getNumber(), programType.getProgramName(), null, null);
    }

    public Week currentWeek() {
        int dayOfWeek = registrationDate.get(DateTimeFieldType.dayOfWeek());
        Period period = new Period(registrationDate, dateUtils.now(), PeriodType.days());
        // substract of -1 => eg., Reg date : 2nd Feb Wed 2011, On 6th Feb Sat, Date difference is 3, DayofWeek is 4= (3+4) 7/7 = 1
        int weeksToAddBasedOnSundayAsStartDay = (period.getDays() + dayOfWeek - 1) / 7;
        return startWeekAndDay.getWeek().add(weeksToAddBasedOnSundayAsStartDay);
    }

    public Day currentDay() {
        String day = dateUtils.now().dayOfWeek().getAsText();
        return Day.valueOf(day.toUpperCase());
    }

    public String programName() {
        return programType.getProgramName();
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
