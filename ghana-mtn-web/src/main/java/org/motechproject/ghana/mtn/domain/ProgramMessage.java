package org.motechproject.ghana.mtn.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.Arrays;

@TypeDiscriminator("doc.type === 'ProgramMessage'")
public class ProgramMessage extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "ProgramMessage";
    private String programKey;
    private String content;
    private WeekAndDay weekAndDay;

    public ProgramMessage() {
    }

    public ProgramMessage(String programKey, String content, WeekAndDay weekAndDay) {
        this.programKey = programKey;
        this.content = content;
        this.weekAndDay = weekAndDay;
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

    public boolean isOf(Week week, Day day) {
        return this.weekAndDay.getWeek().equals(week) && this.weekAndDay.getDay().equals(day);
    }

    @Override
    public String toString() {
        return StringUtils.join(Arrays.asList(programKey, weekAndDay.getWeek(), weekAndDay.getDay(), content), "|");
    }
}