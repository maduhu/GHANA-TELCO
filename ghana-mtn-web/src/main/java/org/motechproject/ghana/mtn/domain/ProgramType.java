package org.motechproject.ghana.mtn.domain;

import org.apache.commons.lang.math.IntRange;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.List;

@TypeDiscriminator("doc.type === 'ProgramType'")
public class ProgramType extends MotechAuditableDataObject implements IProgramType {
    @JsonProperty("type")
    private String type = "ProgramType";
    private Money fee;
    private Integer minWeek;
    private Integer maxWeek;
    private String programName;
    private List<String> shortCodes;

    public ProgramType() {
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

    public ProgramType setProgramName(String programName) {
        this.programName = programName;
        return this;
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

    public Money getFee() {
        return fee;
    }

    public void setFee(Money fee) {
        this.fee = fee;
    }
}
