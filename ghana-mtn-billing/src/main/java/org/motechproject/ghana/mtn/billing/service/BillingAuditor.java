package org.motechproject.ghana.mtn.billing.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.domain.BillStatus;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.repository.AllBillAudits;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillingAuditor {
    private AllBillAudits allBillAudits;

    @Autowired
    public BillingAuditor(AllBillAudits allBillAudits) {
        this.allBillAudits = allBillAudits;
    }

    public void auditError(BillingServiceRequest billingServiceRequest, ValidationError error) {
        allBillAudits.add(new BillAudit(
                billingServiceRequest.getMobileNumber(),
                billingServiceRequest.programName(),
                billingServiceRequest.getProgramType().getFee(),
                BillStatus.FAILURE,
                error.name()));
    }

    public void audit(BillingServiceRequest billingServiceRequest) {
        allBillAudits.add(new BillAudit(
                billingServiceRequest.getMobileNumber(),
                billingServiceRequest.programName(),
                billingServiceRequest.getProgramType().getFee(),
                BillStatus.SUCCESS,
                StringUtils.EMPTY
                ));
    }


}
