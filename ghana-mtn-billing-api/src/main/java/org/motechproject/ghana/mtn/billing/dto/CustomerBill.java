package org.motechproject.ghana.mtn.billing.dto;

import org.motechproject.ghana.mtn.vo.Money;

public class CustomerBill {

    private Money amountCharged;
    private String message;

    public CustomerBill(String value, Money amountCharged) {
        this.message = value;
        this.amountCharged = amountCharged;
    }

    public CustomerBill() {
    }

    public Money getAmountCharged() {
        return amountCharged;
    }

    public Double amountCharged() {
        return amountCharged.getValue();
    }

    public String amountChargedWithCurrency() {
        return amountCharged.toString();
    }

    public String getMessage() {
        return message;
    }
}