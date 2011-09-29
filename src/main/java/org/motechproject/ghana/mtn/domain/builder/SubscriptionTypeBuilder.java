package org.motechproject.ghana.mtn.domain.builder;

import org.motechproject.ghana.mtn.domain.SubscriptionType;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionTypeBuilder extends Builder<SubscriptionType> {
    private List<String> shortCodes;
    private String programName;
    private Integer minWeek;
    private Integer maxWeek;

    public SubscriptionTypeBuilder() {
        super(new SubscriptionType());
        this.shortCodes = new ArrayList<String>();
    }

    public SubscriptionTypeBuilder withMinWeek(Integer minWeek) {
        this.minWeek = minWeek;
        return this;
    }

    public SubscriptionTypeBuilder withMaxWeek(Integer maxWeek) {
        this.maxWeek = maxWeek;
        return this;
    }

    public SubscriptionTypeBuilder withProgramName(String programName) {
        this.programName = programName;
        return this;
    }

    public SubscriptionTypeBuilder withShortCode(String shortCode) {
        this.shortCodes.add(shortCode);
        return this;
    }
}
