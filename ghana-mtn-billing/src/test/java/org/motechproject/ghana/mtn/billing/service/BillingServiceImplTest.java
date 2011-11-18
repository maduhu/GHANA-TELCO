package org.motechproject.ghana.mtn.billing.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.mock.MTNMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.scheduler.MotechSchedulerService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.billing.service.BillingServiceImpl.BILLING_SUCCESSFUL;

public class BillingServiceImplTest {
    private BillingServiceImpl service;
    @Mock
    private MTNMock mtnMock;
    @Mock
    private AllBillAccounts allBillAccounts;
    @Mock
    private MotechSchedulerService schedulerService;
    @Mock
    private BillingScheduler scheduler;
    @Mock
    private BillingAuditor auditor;

    @Before
    public void setUp() {
        initMocks(this);
        service = new BillingServiceImpl(allBillAccounts, scheduler, auditor, mtnMock);
    }

    @Test
    public void shouldReturnErrorResponseIfNotAValidMTNCustomer() {
        BillingServiceRequest request = mock(BillingServiceRequest.class);
        when(request.getMobileNumber()).thenReturn("123");
        when(request.getProgramFeeValue()).thenReturn(12d);
        when(mtnMock.isMtnCustomer("123")).thenReturn(false);

        BillingServiceResponse response = service.checkIfUserHasFunds(request);

        verify(auditor).auditError(request, ValidationError.INVALID_CUSTOMER);
        assertEquals(ValidationError.INVALID_CUSTOMER, response.getValidationErrors().get(0));
    }

    @Test
    public void shouldReturnErrorResponseIfCustomerHasNoFunds() {
        BillingServiceRequest request = mock(BillingServiceRequest.class);
        when(request.getMobileNumber()).thenReturn("123");
        when(request.getProgramFeeValue()).thenReturn(12d);
        when(mtnMock.isMtnCustomer("123")).thenReturn(true);
        when(mtnMock.getBalanceFor("123")).thenReturn(1d);

        BillingServiceResponse response = service.checkIfUserHasFunds(request);

        verify(auditor).auditError(request, ValidationError.INSUFFICIENT_FUNDS);
        assertEquals(ValidationError.INSUFFICIENT_FUNDS, response.getValidationErrors().get(0));
    }

    @Test
    public void shouldContactMTNAndUpdateAccountAndAudit() {
        BillingServiceRequest request = mock(BillingServiceRequest.class);
        IProgramType programType = mock(IProgramType.class);

        String mobileNumber = "123";
        Money charge = new Money(12d);
        when(request.getMobileNumber()).thenReturn(mobileNumber);
        when(request.getProgramFeeValue()).thenReturn(charge.getValue());
        when(request.getProgramType()).thenReturn(programType);
        when(mtnMock.getBalanceFor(mobileNumber)).thenReturn(1d);
        when(mtnMock.chargeCustomer(mobileNumber, charge.getValue())).thenReturn(charge);

        BillingServiceResponse<CustomerBill> response = service.chargeProgramFee(request);

        verify(mtnMock).chargeCustomer("123", 12d);
        verify(auditor).audit(request);
        verify(allBillAccounts).updateFor("123", 1d, programType);
        assertFalse(response.hasErrors());
        assertEquals(charge.getValue(), response.getValue().amountCharged());
        assertEquals(BILLING_SUCCESSFUL, response.getValue().getMessage());
    }

    @Test
    public void shouldRaiseAScheduleUsingPlatformSchedulerOnProcessRegistration() {
        BillingCycleRequest request = mock(BillingCycleRequest.class);
        IProgramType programType = mock(IProgramType.class);

        String mobileNumber = "123";
        Money charge = new Money(12d);
        when(request.getMobileNumber()).thenReturn(mobileNumber);
        when(request.getProgramFeeValue()).thenReturn(charge.getValue());
        when(request.getProgramType()).thenReturn(programType);
        when(mtnMock.getBalanceFor(mobileNumber)).thenReturn(1d);
        when(mtnMock.chargeCustomer(mobileNumber, charge.getValue())).thenReturn(charge);

        BillingServiceResponse<CustomerBill> response = service.startBilling(request);

        verify(scheduler).startFor(request);
        assertEquals(BillingServiceImpl.BILLING_SCHEDULE_STARTED, response.getValue().getMessage());
        assertEquals(charge.getValue(), response.getValue().amountCharged());
    }

    @Test
    public void shouldStopBillingCycleByCallingScheduler(){
        BillingCycleRequest request = mock(BillingCycleRequest.class);

        BillingServiceResponse response = service.stopBilling(request);

        verify(scheduler).stopFor(request);
        assertEquals(BillingServiceImpl.BILLING_SCHEDULE_STOPPED, response.getValue());
    }

    @Test
    public void shouldCallSchedulerForRollOverBilling(){
        BillingCycleRequest request = mock(BillingCycleRequest.class);

        BillingServiceResponse response = service.rollOverBilling(request);

        verify(scheduler).startFor(request);
        assertEquals(BillingServiceImpl.BILLING_ROLLED_OVER, response.getValue());
    }

}