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

import java.util.List;

import static java.util.Arrays.asList;
import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.motechproject.util.DateUtil.setTimeZone;

@TypeDiscriminator("doc.type === 'Subscription'")
public class Subscription extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "Subscription";
    private Subscriber subscriber;
    private ProgramType programType;
    private SubscriptionStatus status;
    private WeekAndDay startWeekAndDay;
    @JsonProperty("lastMsgSentWeekAndDay")
    private WeekAndDay lastMsgSentWeekAndDay;

    private DateTime registrationDate;
    private DateTime cycleStartDate;
    private DateTime billingStartDate;
    private DateUtils dateUtils = new DateUtils();
    @JsonProperty("cycleEndDate")
    private DateTime cycleEndDate;

    public Subscription() {
    }

    public Subscription(Subscriber subscriber, ProgramType programType, SubscriptionStatus status, WeekAndDay startWeekAndDay, DateTime registrationDate) {
        this.subscriber = subscriber;
        this.programType = programType;
        this.status = status;
        this.startWeekAndDay = startWeekAndDay;
        this.registrationDate = registrationDate;
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

    public Subscription setStatus(SubscriptionStatus status) {
        this.status = status;
        return this;
    }

    public WeekAndDay getStartWeekAndDay() {
        return startWeekAndDay;
    }

    public void setStartWeekAndDay(WeekAndDay startWeekAndDay) {
        this.startWeekAndDay = startWeekAndDay;
    }

    public DateTime getRegistrationDate() {
        return setTimeZone(registrationDate);
    }

    public void setRegistrationDate(DateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public CampaignRequest createCampaignRequest() {
        return new CampaignRequest(subscriber.getNumber(), programType.getProgramKey(), null, null);
    }

    public Week currentWeek() {

        DateTime cycleStartDate = getCycleStartDate();
        DateTime currentDateStartDayTime = dateUtils.startOfDay(dateUtils.now());
        if(cycleStartDate.compareTo(currentDateStartDayTime) > 0) return null;

        DateTime cycleStartDateWithStartDayTime = dateUtils.startOfDay(cycleStartDate);
        int daysDiff = new Period(cycleStartDateWithStartDayTime, currentDateStartDayTime, PeriodType.days()).getDays();

        if (daysDiff > 0) {
            int daysToSaturday = daysToSaturday(cycleStartDateWithStartDayTime);
            int daysAfterFirstSaturday = daysDiff > daysToSaturday ? daysDiff - daysToSaturday : 0;
            int weeksAfterFirstSaturday = daysAfterFirstSaturday / 7 + (daysAfterFirstSaturday % 7 > 0 ? 1 : 0);
            return startWeekAndDay.getWeek().add(weeksAfterFirstSaturday);
        }
        return startWeekAndDay.getWeek();
    }

    private int daysToSaturday(DateTime cycleStartDateWithStartDayTime) {
        int dayOfWeek = cycleStartDateWithStartDayTime.get(DateTimeFieldType.dayOfWeek());
        return (dayOfWeek == DateTimeConstants.SUNDAY) ? 6 : SATURDAY - dayOfWeek;
    }

    private DateTime cycleStartDate() {
        return dateUtils.startOfDay(new ProgramMessageCycle().nearestCycleDate(getRegistrationDate()));
    }

    public DateTime billingStartDate(DateTime startDateOfCycle) {
        List<Integer> forDaysToMoveToFirstOfMonth = asList(29, 30, 31);
        startDateOfCycle = startDateOfCycle.withTimeAtStartOfDay();
        if (forDaysToMoveToFirstOfMonth.contains(startDateOfCycle.getDayOfMonth()))
            return startDateOfCycle.monthOfYear().addToCopy(1).withDayOfMonth(1);
        return startDateOfCycle;
    }

    public Subscription updateCycleInfo() {
        updateStartCycle();
        updateCycleEndDate();
        return this;
    }

    private void updateStartCycle() {
        DateTime startDateOfCycle = cycleStartDate();
        this.getStartWeekAndDay().setDay(dateUtils.day(startDateOfCycle));
        this.cycleStartDate = startDateOfCycle;
        this.billingStartDate = billingStartDate(startDateOfCycle);
    }

    private void updateCycleEndDate() {
        int daysToFirstSaturday = daysToSaturday(this.cycleStartDate);
        Integer weeksRemaining = programType.getMaxWeek() - startWeekAndDay.getWeek().getNumber();
        this.cycleEndDate = this.cycleStartDate.dayOfMonth().addToCopy(daysToFirstSaturday + weeksRemaining * 7);
    }

    public Day currentDay() {
        String day = dateUtils.now().dayOfWeek().getAsText();
        return Day.valueOf(day.toUpperCase());
    }

    public String programName() {
        return programType.getProgramName();
    }

    public String programKey() {
        return programType.getProgramKey();
    }

    public void updateLastMessageSent() {
        lastMsgSentWeekAndDay = new WeekAndDay(currentWeek(), currentDay());
    }

    public boolean alreadySent(ProgramMessage subscriptionMessage) {
        return lastMsgSentWeekAndDay != null && subscriptionMessage.getWeekAndDay().isBefore(lastMsgSentWeekAndDay);
    }

    public String subscriberNumber() {
        return subscriber.getNumber();
    }

    public DateTime getBillingStartDate() {
        return setTimeZone(billingStartDate);
    }

    public DateTime getCycleStartDate() {
        return setTimeZone(cycleStartDate);
    }

    public void setCycleStartDate(DateTime cycleStartDate) {
        this.cycleStartDate = cycleStartDate;
    }


    public void setBillingStartDate(DateTime billingStartDate) {
        this.billingStartDate = billingStartDate;
    }

    @JsonIgnore
    public Boolean isCompleted() {
        Week week = currentWeek();
        return week != null && week.getNumber() >= programType.getMaxWeek() && Day.FRIDAY.equals(currentDay());
    }

    public Boolean canRollOff() {
        return programType.canRollOff();
    }

    @JsonIgnore
    public Boolean isPaymentDefaulted() {
        return SubscriptionStatus.PAYMENT_DEFAULT.equals(status);
    }

    public ProgramType rollOverProgramType() {
        return programType.getRollOverProgramType();
    }

    public DateTime getCycleEndDate() {
        return cycleEndDate;
    }
}
