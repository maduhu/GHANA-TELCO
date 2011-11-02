package org.motechproject.ghana.mtn.billing.dto;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.valueobjects.WallTime;

public class DefaultedBillingRequest extends BillingServiceRequest {
    private DateTime cycleStartDate;
    private WallTime frequency;
    private DateTime cycleEndDate;

    public DefaultedBillingRequest(String mobileNumber,
                                   IProgramType programType, DateTime cycleStartDate,
                                   WallTime frequency, DateTime cycleEndDate) {
        super(mobileNumber, programType);
        this.cycleStartDate = cycleStartDate;
        this.frequency = frequency;
        this.cycleEndDate = cycleEndDate;
    }

    public WallTime getFrequency() {
        return frequency;
    }

    public DateTime getCycleEndDate() {
        return cycleEndDate;
    }

    public DateTime getCycleStartDate() {
        return cycleStartDate;
    }
}
