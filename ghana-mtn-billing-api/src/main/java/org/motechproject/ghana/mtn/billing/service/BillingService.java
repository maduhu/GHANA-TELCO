package org.motechproject.ghana.mtn.billing.service;

import org.motechproject.ghana.mtn.billing.dto.*;

public interface BillingService {
    BillingServiceResponse<CustomerBill> chargeAndStartBilling(BillingCycleRequest billingCycleRequest);
    BillingServiceResponse stopBilling(BillingCycleRequest billingCycleRequest);
    BillingServiceResponse rollOverBilling(BillingCycleRollOverRequest billingCycleRollOverRequest);
    BillingServiceResponse<CustomerBill> chargeProgramFee(BillingServiceRequest billingServiceRequest);
    BillingServiceResponse checkIfUserHasFunds(BillingServiceRequest billingServiceRequest);
    BillingServiceResponse startDefaultedBillingSchedule(DefaultedBillingRequest defaultedBillingRequest);
    BillingServiceResponse stopDefaultedBillingSchedule(DefaultedBillingRequest defaultedBillingRequest);
}
