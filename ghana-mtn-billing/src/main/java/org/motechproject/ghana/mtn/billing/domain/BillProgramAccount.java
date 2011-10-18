package org.motechproject.ghana.mtn.billing.domain;

import org.motechproject.ghana.mtn.vo.Money;

public class BillProgramAccount {
    private String programKey;
    private Money fee;

    public BillProgramAccount() {
    }

    public BillProgramAccount(String programKey, Money fee) {
        this.programKey = programKey;
        this.fee = fee;
    }

    public void setProgramKey(String programKey) {
        this.programKey = programKey;
    }

    public String getProgramKey() {
        return programKey;
    }

    public void setFee(Money fee) {
        this.fee = fee;
    }

    public Money getFee() {
        return fee;
    }
}
