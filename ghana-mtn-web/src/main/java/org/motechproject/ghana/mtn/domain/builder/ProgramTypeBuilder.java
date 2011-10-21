package org.motechproject.ghana.mtn.domain.builder;

import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.vo.Money;

import java.util.ArrayList;
import java.util.List;

public class ProgramTypeBuilder extends Builder<ProgramType> {
    private List<String> shortCodes;
    private String programName;
    private Integer minWeek;
    private Integer maxWeek;
    private Money fee;
    private ProgramType rollOverProgramType;

    public ProgramTypeBuilder() {
        super(new ProgramType());
        this.shortCodes = new ArrayList<String>();
    }

    public ProgramTypeBuilder withMinWeek(Integer minWeek) {
        this.minWeek = minWeek;
        return this;
    }

    public ProgramTypeBuilder withMaxWeek(Integer maxWeek) {
        this.maxWeek = maxWeek;
        return this;
    }

    public ProgramTypeBuilder withProgramName(String programName) {
        this.programName = programName;
        return this;
    }

    public ProgramTypeBuilder withShortCode(String... shortCodes) {
        for (String shortCode : shortCodes)
            this.shortCodes.add(shortCode);
        return this;
    }

    public ProgramTypeBuilder withFee(Money fee) {
        this.fee = fee;
        return this;
    }

    public ProgramTypeBuilder willRollOverProgramType(ProgramType rollOverProgramType) {
        this.rollOverProgramType = rollOverProgramType;
        return this;
    }
}
