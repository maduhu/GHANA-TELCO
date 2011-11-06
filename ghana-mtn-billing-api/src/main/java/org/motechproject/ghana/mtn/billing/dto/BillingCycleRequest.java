package org.motechproject.ghana.mtn.billing.dto;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.IProgramType;

public class BillingCycleRequest extends BillingServiceRequest {

    private DateTime cycleStartDate;
    private DateTime cycleEndDate;

    public BillingCycleRequest(String mobileNumber, IProgramType programType, DateTime cycleStartDate, DateTime cycleEndDate) {
        super(mobileNumber, programType);
        this.cycleStartDate = cycleStartDate;
        this.cycleEndDate = cycleEndDate;
    }

    public DateTime getCycleStartDate() {
        return cycleStartDate;
    }

    public DateTime getCycleEndDate() {
        return cycleEndDate;
    }
}