package org.motechproject.ghana.mtn.billing.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'BillAudit'")
public class BillAudit extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "BillAudit";
    private String mobileNumber;
    private Money amountToCharge;
    private BillStatus billStatus;
    private String failureReason;
    private DateTime date;
    public BillAudit() {
    }

    public BillAudit(String mobileNumber, Money amountToCharge, BillStatus billStatus, String failureReason) {
        this.mobileNumber = mobileNumber;
        this.amountToCharge = amountToCharge;
        this.billStatus = billStatus;
        this.failureReason = failureReason;
        this.date = DateTime.now();
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public Money getAmountToCharge() {
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

    public void setAmountToCharge(Money amountToCharge) {
        this.amountToCharge = amountToCharge;
    }

    public void setBillStatus(BillStatus billStatus) {
        this.billStatus = billStatus;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }
}
