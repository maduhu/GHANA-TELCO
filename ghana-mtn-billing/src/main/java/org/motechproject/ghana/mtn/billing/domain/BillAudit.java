package org.motechproject.ghana.mtn.billing.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'BillAudit'")
public class BillAudit extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "BillAudit";
    private String mobileNumber;
    private double amountToCharge;
    private BillStatus billStatus;
    private String failureReason;

    public BillAudit() {
    }

    public BillAudit(String mobileNumber, double amountToCharge, BillStatus billStatus, String failureReason, LocalDate dateTime) {
        this.mobileNumber = mobileNumber;
        this.amountToCharge = amountToCharge;
        this.billStatus = billStatus;
        this.failureReason = failureReason;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public Double getAmountToCharge() {
        return amountToCharge;
    }

    public BillStatus getBillStatus() {
        return billStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setAmountToCharge(double amountToCharge) {
        this.amountToCharge = amountToCharge;
    }

    public void setBillStatus(BillStatus billStatus) {
        this.billStatus = billStatus;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
