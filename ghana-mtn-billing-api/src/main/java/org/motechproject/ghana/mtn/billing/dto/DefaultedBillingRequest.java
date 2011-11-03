package org.motechproject.ghana.mtn.billing.dto;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.valueobjects.WallTimeUnit;

public class DefaultedBillingRequest extends BillingServiceRequest {
    private DateTime cycleStartDate;
    private WallTimeUnit frequency;
    private DateTime cycleEndDate;

    public DefaultedBillingRequest(String mobileNumber,
                                   IProgramType programType, DateTime cycleStartDate,
                                   WallTimeUnit frequency, DateTime cycleEndDate) {
        super(mobileNumber, programType);
        this.cycleStartDate = cycleStartDate;
        this.frequency = frequency;
        this.cycleEndDate = cycleEndDate;
    }

    public DefaultedBillingRequest(String mobileNumber,
                                   IProgramType programType ,WallTimeUnit frequency) {
        this(mobileNumber, programType, null, frequency, null);
    }

    public WallTimeUnit getFrequency() {
        return frequency;
    }

    public DateTime getCycleEndDate() {
        return cycleEndDate;
    }

    public DateTime getCycleStartDate() {
        return cycleStartDate;
    }
}
