package org.motechproject.ghana.telco.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.telco.domain.vo.Week;
import org.motechproject.ghana.telco.domain.vo.WeekAndDay;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.Arrays;

@TypeDiscriminator("doc.type === 'ProgramMessage'")
public class ProgramMessage extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "ProgramMessage";
    @JsonProperty
    private String messageKey;
    private String programKey;
    private String content;
    private WeekAndDay weekAndDay;

    public ProgramMessage() {
    }

    public ProgramMessage(String messageKey, String programKey, String content, WeekAndDay weekAndDay) {
        this.messageKey = messageKey;
        this.programKey = programKey;
        this.content = content;
        this.weekAndDay = weekAndDay;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getProgramKey() {
        return programKey;
    }

    public void setProgramKey(String programKey) {
        this.programKey = programKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public WeekAndDay getWeekAndDay() {
        return weekAndDay;
    }

    public void setWeekAndDay(WeekAndDay weekAndDay) {
        this.weekAndDay = weekAndDay;
    }

    public boolean isOf(Week week, DayOfWeek day) {
        return this.weekAndDay.getWeek().equals(week) && this.weekAndDay.getDay().equals(day);
    }

    @Override
    public String toString() {
        return StringUtils.join(Arrays.asList(programKey, weekAndDay.getWeek(), weekAndDay.getDay(), content), "|");
    }
}
