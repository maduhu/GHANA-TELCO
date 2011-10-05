package org.motechproject.ghana.mtn.billing;

import org.drools.core.util.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.domain.BillStatus;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.ValidationError;
import org.motechproject.ghana.mtn.billing.matcher.BillAuditMatcher;
import org.motechproject.ghana.mtn.billing.mock.MTNBillingSystemMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.billing.repository.AllBillAudits;
import org.motechproject.ghana.mtn.billing.service.BillingServiceImpl;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillingServiceImplTest {
    private BillingServiceImpl billingService;
    @Mock
    private MTNBillingSystemMock mtnBillingSystemMock;
    @Mock
    private AllBillAudits allBillAudits;
    @Mock
    private AllBillAccounts allBillAccounts;

    @Before
    public void setUp() {
        initMocks(this);
        billingService = new BillingServiceImpl(mtnBillingSystemMock, allBillAudits, allBillAccounts);
    }

    @Test
    public void ShouldNotProceedWithBillingForNonMtnCustomerAndPersistFailureBillAudit() {
        String mobileNumber = "1234567890";
        double amountToCharge = 0.60;
        Double currentBalance = 10D;
        IProgramType programType = getProgramType();

        when(mtnBillingSystemMock.isMtnCustomer(mobileNumber)).thenReturn(false);

        BillingServiceResponse billingServiceResponse = billingService.chargeSubscriptionFee(new BillingServiceRequest(mobileNumber, getProgramType()));

        assertFalse(billingServiceResponse.isValid());
        assertEquals(billingServiceResponse.getValidationErrors(), Arrays.asList(ValidationError.NOT_A_VALID_CUSTOMER));
        verify(mtnBillingSystemMock).isMtnCustomer(mobileNumber);
        verify(mtnBillingSystemMock, never()).chargeCustomer(mobileNumber, amountToCharge);
        verify(allBillAccounts, never()).updateBillAccount(mobileNumber, currentBalance, programType);
        verify(allBillAudits).add(argThat(new BillAuditMatcher(new BillAudit(mobileNumber, amountToCharge, BillStatus.FAILURE, ValidationError.NOT_A_VALID_CUSTOMER.name(), DateUtil.today()))));
    }

    @Test
    public void ShouldNotProceedForMtnCustomerWithInsufficientBalanceAndPersistFailureBillAudit() {
        String mobileNumber = "1234567890";
        double amountToCharge = 0.60;
        Double currentBalance = 2D;

        when(mtnBillingSystemMock.isMtnCustomer(mobileNumber)).thenReturn(true);
        when(mtnBillingSystemMock.getAvailableBalance(mobileNumber)).thenReturn(0D);

        BillingServiceResponse billingServiceResponse = billingService.chargeSubscriptionFee(new BillingServiceRequest(mobileNumber, getProgramType()));

        assertFalse(billingServiceResponse.isValid());
        assertEquals(billingServiceResponse.getValidationErrors(), Arrays.asList(ValidationError.INSUFFICIENT_FUND));

        verify(mtnBillingSystemMock).isMtnCustomer(mobileNumber);
        verify(mtnBillingSystemMock).getAvailableBalance(mobileNumber);
        verify(mtnBillingSystemMock, never()).chargeCustomer(mobileNumber, amountToCharge);
        verify(allBillAccounts, never()).updateBillAccount(mobileNumber, currentBalance, getProgramType());
        verify(allBillAudits).add(argThat(new BillAuditMatcher(new BillAudit(mobileNumber, amountToCharge, BillStatus.FAILURE, ValidationError.INSUFFICIENT_FUND.name(), DateUtil.today()))));
    }

    @Test
    public void ShouldChargeMrnCustomerAndGiveAValidResponseForCutomerWithValidFundsAndPersistSuccessBillAudit() {
        String mobileNumber = "1234567890";
        double amountToCharge = 0.60;
        Double currentBalance = 2D;

        when(mtnBillingSystemMock.isMtnCustomer(mobileNumber)).thenReturn(true);
        when(mtnBillingSystemMock.getAvailableBalance(mobileNumber)).thenReturn(2D);

        IProgramType programType = getProgramType();
        BillingServiceResponse billingServiceResponse = billingService.chargeSubscriptionFee(new BillingServiceRequest(mobileNumber, programType));

        assertTrue(billingServiceResponse.isValid());
        assertTrue(billingServiceResponse.getValidationErrors().isEmpty());

        verify(mtnBillingSystemMock).isMtnCustomer(mobileNumber);
        verify(mtnBillingSystemMock).getAvailableBalance(mobileNumber);
        verify(mtnBillingSystemMock).chargeCustomer(mobileNumber, amountToCharge);
        verify(allBillAccounts).updateBillAccount(mobileNumber, currentBalance, programType);
        verify(allBillAudits).add(argThat(new BillAuditMatcher(new BillAudit(mobileNumber, amountToCharge, BillStatus.SUCCESS, StringUtils.EMPTY, DateUtil.today()))));
    }

    private IProgramType getProgramType() {
        return new IProgramType() {
            @Override
            public String getProgramName() {
                return null;
            }

            @Override
            public List<String> getShortCodes() {
                return null;
            }

            @Override
            public Integer getMinWeek() {
                return null;
            }

            @Override
            public Integer getMaxWeek() {
                return null;
            }

            @Override
            public Double getFee() {
                return 0.6D;
            }
        };
    }
}