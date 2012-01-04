package org.motechproject.ghana.telco.domain;

import org.apache.commons.lang.math.IntRange;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.List;

@TypeDiscriminator("doc.type === 'ProgramType'")
public class ProgramType extends MotechAuditableDataObject {

    @JsonIgnore
    public static final String PREGNANCY = "PREGNANCY";
    @JsonIgnore
    public static final String CHILDCARE = "CHILDCARE";

    @JsonProperty("type")
    private String type = "ProgramType";
    private Integer minWeek;
    private Integer maxWeek;
    private String programName;
    private ProgramType rollOverProgramType;
    private String programKey;
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

    public String getProgramKey() {
        return programKey;
    }

    public void setProgramKey(String programKey) {
        this.programKey = programKey;
    }

    public Boolean canRollOff() {
        return rollOverProgramType != null;
    }

    public ProgramType getRollOverProgramType() {
        return rollOverProgramType;
    }

    public void setRollOverProgramType(ProgramType rollOverProgramType) {
        this.rollOverProgramType = rollOverProgramType;
    }
}
