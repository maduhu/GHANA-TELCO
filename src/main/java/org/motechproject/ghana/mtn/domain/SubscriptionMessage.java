package org.motechproject.ghana.mtn.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.Arrays;

@TypeDiscriminator("doc.type === 'SubscriptionMessage'")
public class SubscriptionMessage extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "SubscriptionMessage";
    private String programName;
    private String content;
    private Week week;
    private Day day;

    public SubscriptionMessage() {
    }

    public SubscriptionMessage(String programName, String content, Week week, Day day) {
        this.programName = programName;
        this.content = content;
        this.week = week;
        this.day = day;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
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

    public boolean isOf(Week week, Day day) {
        return this.week.equals(week) && this.day.equals(day);
    }

    @Override
    public String toString() {
        return StringUtils.join(Arrays.asList(programName, week, day, content), "|");
    }
}