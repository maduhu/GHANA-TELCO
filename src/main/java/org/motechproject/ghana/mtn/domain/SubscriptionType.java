package org.motechproject.ghana.mtn.domain;

import org.apache.commons.lang.math.IntRange;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.List;

@TypeDiscriminator("doc.type === 'SubscriptionType'")
public class SubscriptionType extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "SubscriptionType";
    private List<String> shortCodes;
    private Integer minWeek;
    private Integer maxWeek;
    private String programName;

    public SubscriptionType() {
    }

    public boolean isInRange(Integer startFrom) {
        IntRange weekRange = new IntRange(minWeek, maxWeek);
        return weekRange.containsInteger(startFrom);
    }

    public String getProgramName() {
        return programName;
    }

    public List<String> getShortCodes() {
        return shortCodes;
    }

    public void setShortCodes(List<String> shortCodes) {
        this.shortCodes = shortCodes;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Integer getMinWeek() {
        return minWeek;
    }

    public void setMinWeek(Integer minWeek) {
        this.minWeek = minWeek;
    }

    public Integer getMaxWeek() {
        return maxWeek;
    }

    public void setMaxWeek(Integer maxWeek) {
        this.maxWeek = maxWeek;
    }
}
