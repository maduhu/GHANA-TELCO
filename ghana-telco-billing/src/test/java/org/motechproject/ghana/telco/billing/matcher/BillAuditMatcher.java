package org.motechproject.ghana.telco.billing.matcher;

import org.mockito.ArgumentMatcher;
import org.motechproject.ghana.telco.billing.domain.BillAudit;

public class BillAuditMatcher extends ArgumentMatcher<BillAudit> {
    private BillAudit billAudit;

    public BillAuditMatcher(BillAudit billAudit) {
        this.billAudit = billAudit;
    }

    @Override
    public boolean matches(Object o) {
        BillAudit billAudit = (BillAudit) o;
        return billAudit.getAmountCharged().getValue().equals(this.billAudit.getAmountCharged().getValue())
                && billAudit.getBillStatus().equals(this.billAudit.getBillStatus())
                && billAudit.getMobileNumber().equals(this.billAudit.getMobileNumber())
                && billAudit.getFailureReason().equals(this.billAudit.getFailureReason());
    }
}
