package org.motechproject.ghana.mtn.billing.mock;

import org.springframework.stereotype.Component;

@Component
public class MTNBillingSystemMock {
    private boolean mtnCustomer;

    public Double getAvailableBalance(String mobileNumber) {
        return 0D;
    }

    public boolean isMtnCustomer(String mobileNumber) {
        return mtnCustomer;
    }

    public void chargeCustomer(String mobileNumber, double amountToCharge) {
    }
}