package org.motechproject.ghana.telco.domain;

import org.apache.commons.lang.math.IntRange;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.telco.exception.InvalidMonthException;
import org.motechproject.model.MotechBaseDataObject;

import java.util.List;

@TypeDiscriminator("doc.type === 'ProgramType'")
public class ProgramType extends MotechBaseDataObject {

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

    public static final int TOTAL_WEEKS_FOR_CHILD_CARE = 52;
    public static final int TOTAL_MONTHS = 12;

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

    public ProgramType setProgramKey(String programKey) {
        this.programKey = programKey;
        return this;
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

    public Integer weekFor(int weekNumber) throws InvalidMonthException {
        if (programKey.equals(CHILDCARE))
            return convertMonthToWeek(weekNumber);
        return weekNumber;
    }

    private Integer convertMonthToWeek(int weekNumber) throws InvalidMonthException {
        if (weekNumber <= 0 || weekNumber > 12)
            throw new InvalidMonthException("Child Care message invalid month value: " + weekNumber);

        return weekNumber == 1 ? 1 :
                Math.round(((weekNumber - 1) * TOTAL_WEEKS_FOR_CHILD_CARE / TOTAL_MONTHS));
    }
}
