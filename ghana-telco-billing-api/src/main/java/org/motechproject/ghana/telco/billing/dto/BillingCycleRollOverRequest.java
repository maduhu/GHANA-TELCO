package org.motechproject.ghana.telco.billing.dto;

public class BillingCycleRollOverRequest {
    private BillingCycleRequest fromRequest;
    private BillingCycleRequest toRequest;

    public BillingCycleRollOverRequest(BillingCycleRequest fromRequest, BillingCycleRequest toRequest) {
        this.fromRequest = fromRequest;
        this.toRequest = toRequest;
    }

    public BillingCycleRequest getFromRequest() {
        return fromRequest;
    }

    public BillingCycleRequest getToRequest() {
        return toRequest;
    }
}