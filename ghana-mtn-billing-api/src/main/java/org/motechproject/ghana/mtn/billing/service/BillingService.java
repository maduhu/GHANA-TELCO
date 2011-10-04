package org.motechproject.ghana.mtn.billing.service;

import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;

public interface BillingService {
    BillingServiceResponse chargeSubscriptionFee(BillingServiceRequest billingServiceRequest);
}
