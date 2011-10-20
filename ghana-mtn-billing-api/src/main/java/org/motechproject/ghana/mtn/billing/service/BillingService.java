package org.motechproject.ghana.mtn.billing.service;

import org.motechproject.ghana.mtn.billing.dto.*;

public interface BillingService {
    BillingServiceResponse<CustomerBill> startBilling(BillingCycleRequest billingCycleRequest);
    BillingServiceResponse stopBilling(BillingCycleRequest billingCycleRequest);
    BillingServiceResponse rollOverBilling(BillingCycleRollOverRequest billingCycleRollOverRequest);
    BillingServiceResponse<CustomerBill> chargeProgramFee(BillingServiceRequest billingServiceRequest);
    BillingServiceResponse checkIfUserHasFunds(BillingServiceRequest billingServiceRequest);
}
