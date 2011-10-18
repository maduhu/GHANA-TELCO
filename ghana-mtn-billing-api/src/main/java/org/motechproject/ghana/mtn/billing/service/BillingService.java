package org.motechproject.ghana.mtn.billing.service;

import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;

public interface BillingService {
    BillingServiceResponse<CustomerBill> startBilling(BillingCycleRequest billingCycleRequest);
    BillingServiceResponse stopBilling(BillingCycleRequest billingCycleRequest);
    BillingServiceResponse rollOverBilling(BillingCycleRequest billingCycleRequest);
    BillingServiceResponse<CustomerBill> chargeProgramFee(BillingServiceRequest billingServiceRequest);
    BillingServiceResponse checkIfUserHasFunds(BillingServiceRequest billingServiceRequest);
}
