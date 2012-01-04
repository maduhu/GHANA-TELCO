package org.motechproject.ghana.telco.billing.dto;

import org.motechproject.ghana.telco.domain.IProgramType;
import org.motechproject.ghana.telco.vo.Money;

public class BillingServiceRequest {
    private String mobileNumber;
    private IProgramType programType;

    public BillingServiceRequest(String mobileNumber, IProgramType programType) {
        this.mobileNumber = mobileNumber;
        this.programType = programType;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public IProgramType getProgramType() {
        return programType;
    }

    public String programKey() {
        return programType.getProgramKey();
    }

    public Money getProgramFee() {
        return programType.getFee();
    }

    public Double getProgramFeeValue() {
        return programType.getFee().getValue();
    }
}