package org.motechproject.ghana.mtn.billing.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.domain.BillStatus;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.repository.AllBillAudits;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.dto.Money;
import org.motechproject.ghana.mtn.validation.ValidationError;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class BillingAuditorTest {
    private BillingAuditor auditor;
    @Mock
    private AllBillAudits allBillingAudits;

    @Before
    public void setUp() {
        initMocks(this);
        auditor = new BillingAuditor(allBillingAudits);
    }

    @Test
    public void shouldAuditForSuccessCondition() {
        IProgramType programType = mock(IProgramType.class);
        BillingServiceRequest request = new BillingServiceRequest("123", programType);
        Money fee = new Money(12d);

        when(programType.getFee()).thenReturn(fee);

        auditor.audit(request);

        ArgumentCaptor<BillAudit> captor = ArgumentCaptor.forClass(BillAudit.class);
        verify(allBillingAudits).add(captor.capture());

        BillAudit captured = captor.getValue();
        assertEquals("123", captured.getMobileNumber());
        assertEquals(fee, captured.getAmountToCharge());
        assertEquals(BillStatus.SUCCESS, captured.getBillStatus());
        assertEquals(StringUtils.EMPTY, captured.getFailureReason());

    }

    @Test
    public void shouldAuditForErrorCondition() {
        IProgramType programType = mock(IProgramType.class);
        BillingServiceRequest request = new BillingServiceRequest("123", programType);
        Money fee = new Money(12d);

        when(programType.getFee()).thenReturn(fee);

        auditor.auditError(request, ValidationError.INSUFFICIENT_FUND);

        ArgumentCaptor<BillAudit> captor = ArgumentCaptor.forClass(BillAudit.class);
        verify(allBillingAudits).add(captor.capture());

        BillAudit captured = captor.getValue();
        assertEquals("123", captured.getMobileNumber());
        assertEquals(fee, captured.getAmountToCharge());
        assertEquals(BillStatus.FAILURE, captured.getBillStatus());
        assertEquals(ValidationError.INSUFFICIENT_FUND.name(), captured.getFailureReason());

    }


}
