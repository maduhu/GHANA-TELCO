package org.motechproject.ghana.mtn.billing.service;

import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.RegistrationBillingRequest;

public interface BillingService {
    BillingServiceResponse processRegistration(RegistrationBillingRequest registrationBillingRequest);
    BillingServiceResponse chargeSubscriptionFee(BillingServiceRequest billingServiceRequest);
    BillingServiceResponse hasAvailableFundForProgram(BillingServiceRequest billingServiceRequest);
}
