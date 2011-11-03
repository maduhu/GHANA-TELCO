package org.motechproject.ghana.mtn.billing.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.*;
import org.motechproject.ghana.mtn.billing.exception.InsufficientFundsException;
import org.motechproject.ghana.mtn.billing.mock.MTNMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.valueobjects.WallTimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
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
    public void shouldContactMTNAndUpdateAccountAndAudit() throws InsufficientFundsException {
        BillingServiceRequest request = mock(BillingServiceRequest.class);
        IProgramType programType = mock(IProgramType.class);

        String mobileNumber = "123";
        Money charge = new Money(12d);
        when(request.getMobileNumber()).thenReturn(mobileNumber);
        when(request.getProgramFeeValue()).thenReturn(charge.getValue());
        when(request.getProgramType()).thenReturn(programType);
        when(mtnMock.getBalanceFor(mobileNumber)).thenReturn(1d);
        when( mtnMock.chargeCustomer(mobileNumber, charge.getValue())).thenReturn(charge);

        BillingServiceResponse<CustomerBill> response = service.chargeProgramFee(request);

        verify(mtnMock).chargeCustomer("123", 12d);
        verify(auditor).audit(request);
        verify(allBillAccounts).updateFor("123", 1d, programType);
        assertFalse(response.hasErrors());
        assertEquals(charge.getValue(), response.getValue().amountCharged());
        assertEquals(BILLING_SUCCESSFUL, response.getValue().getMessage());
    }

    @Test
    public void shouldRaiseAScheduleUsingPlatformSchedulerOnProcessRegistration() throws InsufficientFundsException {
        BillingCycleRequest request = mock(BillingCycleRequest.class);
        IProgramType programType = mock(IProgramType.class);

        String mobileNumber = "123";
        Money charge = new Money(12d);
        when(request.getMobileNumber()).thenReturn(mobileNumber);
        when(request.getProgramFeeValue()).thenReturn(charge.getValue());
        when(request.getProgramType()).thenReturn(programType);
        when(mtnMock.getBalanceFor(mobileNumber)).thenReturn(1d);
        when(mtnMock.chargeCustomer(mobileNumber, charge.getValue())).thenReturn(charge);

        BillingServiceResponse<CustomerBill> response = service.chargeAndStartBilling(request);

        verify(scheduler).startFor(request);
        assertEquals(BillingServiceImpl.BILLING_SCHEDULE_STARTED, response.getValue().getMessage());
        assertEquals(charge.getValue(), response.getValue().amountCharged());
    }

    @Test
    public void shouldContainValidationErrorsWhenUserDoesNotHaveInsufficientFund() throws InsufficientFundsException {
        String mobileNumber = "1234567890";
        IProgramType programType = mock(IProgramType.class);

        when(mtnMock.chargeCustomer(mobileNumber, 2L)).thenThrow(new InsufficientFundsException());
        when(programType.getFee()).thenReturn(new Money(2D));

        BillingServiceResponse<CustomerBill> response = service.chargeProgramFee(new BillingServiceRequest(mobileNumber, programType));
        assertThat(response.getValidationErrors().get(0), is(ValidationError.INSUFFICIENT_FUNDS));
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
        BillingCycleRollOverRequest request = new BillingCycleRollOverRequest(mock(BillingCycleRequest.class), mock(BillingCycleRequest.class));

        BillingServiceResponse response = service.rollOverBilling(request);

        verify(scheduler).stopFor(request.getFromRequest());
        verify(scheduler).startFor(request.getToRequest());
        assertEquals(BillingServiceImpl.BILLING_ROLLED_OVER, response.getValue());
    }

    @Test
    public void shouldNotRollOverBillingWhenStopBillingCycleFails(){
        BillingCycleRollOverRequest request = new BillingCycleRollOverRequest(mock(BillingCycleRequest.class), mock(BillingCycleRequest.class));

        service = spy(service);
        BillingServiceResponse stopBillingResponse = new BillingServiceResponse();
        stopBillingResponse.addError(ValidationError.INVALID_CUSTOMER);

        doReturn(stopBillingResponse).when(service).stopBilling(request.getFromRequest());
        service.rollOverBilling(request);

        verify(scheduler, never()).startFor(request.getToRequest());
    }

    @Test
    public void shouldStartDefaultBillingSchedule() {
        String mobileNumber = "1234567890";
        IProgramType programType = mock(IProgramType.class);
        DefaultedBillingRequest request = new DefaultedBillingRequest(mobileNumber,
                programType, DateTime.now(), WallTimeUnit.Day, DateTime.now().dayOfMonth().addToCopy(1));

        BillingServiceResponse response = service.startDefaultedBillingSchedule(request);

        assertThat(response.hasErrors(), is(false));
        verify(scheduler).startDefaultedBillingSchedule(request);
    }
    
    @Test
    public void shouldStopDefaultedBillingSchedule(){
        DefaultedBillingRequest request = mock(DefaultedBillingRequest.class);

        BillingServiceResponse response = service.stopDefaultedBillingSchedule(request);

        verify(scheduler).stop(request);
        assertEquals(BillingServiceImpl.BILLING_SCHEDULE_STOPPED, response.getValue());
    }
}
