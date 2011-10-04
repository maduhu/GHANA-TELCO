package org.motechproject.ghana.mtn.billing.domain;

public class BillProgramAccount {
    private String programName;
    private Double fee;

    public BillProgramAccount() {
    }

    public BillProgramAccount(String programName, Double fee) {
        this.programName = programName;
        this.fee = fee;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getFee() {
        return fee;
    }
}
