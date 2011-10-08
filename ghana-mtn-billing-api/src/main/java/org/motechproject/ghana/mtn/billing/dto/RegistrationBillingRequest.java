package org.motechproject.ghana.mtn.billing.dto;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.mtn.domain.IProgramType;

public class RegistrationBillingRequest extends BillingServiceRequest {

    private DateTime cycleStartDate;

   public RegistrationBillingRequest(String mobileNumber, IProgramType programType, DateTime cycleStartDate) {
       super(mobileNumber,programType);
       this.cycleStartDate = cycleStartDate;
   }

    public DateTime getCycleStartDate() {
        return cycleStartDate;
    }
}