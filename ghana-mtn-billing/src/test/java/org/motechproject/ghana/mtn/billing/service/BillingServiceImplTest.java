package org.motechproject.ghana.mtn.billing.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.mock.MTNMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.scheduler.MotechSchedulerService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
        when(request.getFeeForProgram()).thenReturn(12d);
        when(mtnMock.isMtnCustomer("123")).thenReturn(false);

        BillingServiceResponse response = service.checkIfUserHasFunds(request);

        verify(auditor).auditError(request, ValidationError.INVALID_CUSTOMER);
        assertEquals(ValidationError.INVALID_CUSTOMER, response.getValidationErrors().get(0));
    }

    @Test
    public void shouldReturnErrorResponseIfCustomerHasNoFunds() {
        BillingServiceRequest request = mock(BillingServiceRequest.class);
        when(request.getMobileNumber()).thenReturn("123");
        when(request.getFeeForProgram()).thenReturn(12d);
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

        when(request.getMobileNumber()).thenReturn("123");
        when(request.getFeeForProgram()).thenReturn(12d);
        when(request.getProgramType()).thenReturn(programType);
        when(mtnMock.getBalanceFor("123")).thenReturn(1d);


        BillingServiceResponse response = service.chargeProgramFee(request);

        verify(mtnMock).chargeCustomer("123", 12d);
        verify(auditor).audit(request);
        verify(allBillAccounts).updateFor("123", 1d, programType);
        assertFalse(response.hasErrors());
    }

    @Test
    public void shouldRaiseAScheduleUsingPlatformSchedulerOnProcessRegistration() {
        BillingCycleRequest request = mock(BillingCycleRequest.class);
        IProgramType programType = mock(IProgramType.class);

        when(request.getMobileNumber()).thenReturn("123");
        when(request.getFeeForProgram()).thenReturn(12d);
        when(request.getProgramType()).thenReturn(programType);
        when(mtnMock.getBalanceFor("123")).thenReturn(1d);

        BillingServiceResponse response = service.startBillingCycle(request);

        verify(scheduler).startFor(request);
        assertEquals(BillingServiceImpl.BILLING_SCHEDULE_STARTED, response.getValue());
    }

    @Test
    public void shouldStopBillingCycleByCallingScheduler(){
        BillingCycleRequest request = mock(BillingCycleRequest.class);

        BillingServiceResponse response = service.stopBillingCycle(request);

        verify(scheduler).stopFor(request);
        assertEquals(BillingServiceImpl.BILLING_SCHEDULE_STOPPED, response.getValue());
    }

}