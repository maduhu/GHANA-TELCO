package org.motechproject.ghana.mtn.billing.domain;

import org.motechproject.ghana.mtn.vo.Money;

public class BillProgramAccount {
    private String programName;
    private Money fee;

    public BillProgramAccount() {
    }

    public BillProgramAccount(String programName, Money fee) {
        this.programName = programName;
        this.fee = fee;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setFee(Money fee) {
        this.fee = fee;
    }

    public Money getFee() {
        return fee;
    }
}
