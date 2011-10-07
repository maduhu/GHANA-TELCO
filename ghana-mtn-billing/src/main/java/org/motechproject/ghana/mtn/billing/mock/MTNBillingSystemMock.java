package org.motechproject.ghana.mtn.billing.mock;

import org.springframework.stereotype.Component;

@Component
public class MTNBillingSystemMock {
    public Double getAvailableBalance(String mobileNumber) {
        if (mobileNumber.equals("1234567890"))
            return 1D;
        return 0D;
    }

    public boolean isMtnCustomer(String mobileNumber) {
        return mobileNumber.equals("1234567890");
    }

    public void chargeCustomer(String mobileNumber, double amountToCharge) {
    }
}
