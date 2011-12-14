package org.motechproject.ghana.mtn.billing.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'BillAudit'")
public class BillAudit extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "BillAudit";
    private String mobileNumber;
    private Money amountCharged;
    private BillStatus billStatus;
    private String failureReason;
    private DateTime date;
    private String program;

    public BillAudit() {
    }

    public BillAudit(String mobileNumber, String program, Money amountCharged, BillStatus billStatus, String failureReason) {
        this.mobileNumber = mobileNumber;
        this.program = program;
        this.amountCharged = amountCharged;
        this.billStatus = billStatus;
        this.failureReason = failureReason;
        this.date = DateTime.now();
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public Money getAmountCharged() {
        return amountCharged;
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

    public void setAmountCharged(Money amountCharged) {
        this.amountCharged = amountCharged;
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

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}
