package org.motechproject.ghana.mtn.billing.dto;

import org.joda.time.LocalDate;
import org.motechproject.ghana.mtn.domain.IProgramType;

public class RegistrationBillingRequest extends BillingServiceRequest {

    private LocalDate cycleStartDate;

   public RegistrationBillingRequest(String mobileNumber, IProgramType programType, LocalDate cycleStartDate) {
       super(mobileNumber,programType);
       this.cycleStartDate = cycleStartDate;
   }

    public LocalDate getCycleStartDate() {
        return cycleStartDate;
    }

    public void setCycleStartDate(LocalDate cycleStartDate) {
        this.cycleStartDate = cycleStartDate;
    }
}