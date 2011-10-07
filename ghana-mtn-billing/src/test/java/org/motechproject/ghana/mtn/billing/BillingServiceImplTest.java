package org.motechproject.ghana.mtn.billing;

import org.drools.core.util.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.domain.BillStatus;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.RegistrationBillingRequest;
import org.motechproject.ghana.mtn.dto.Money;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.ghana.mtn.billing.matcher.BillAuditMatcher;
import org.motechproject.ghana.mtn.billing.mock.MTNBillingSystemMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.billing.repository.AllBillAudits;
import org.motechproject.ghana.mtn.billing.service.BillingServiceImpl;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.util.DateUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.billing.Constants.BillingScheduler.EXTERNAL_ID_KEY;
import static org.motechproject.ghana.mtn.billing.Constants.BillingScheduler.PROGRAM;

public class BillingServiceImplTest {
    private BillingServiceImpl service;
    @Mock
    private MTNBillingSystemMock mtnBillingSystemMock;
    @Mock
    private AllBillAudits allBillAudits;
    @Mock
    private AllBillAccounts allBillAccounts;
    @Mock
    private MotechSchedulerService schedulerService;
    @Before
    public void setUp() {
        initMocks(this);
        service = new BillingServiceImpl(mtnBillingSystemMock, schedulerService, allBillAudits, allBillAccounts);
    }

    @Test
    public void ShouldNotProceedWithBillingForNonMtnCustomerAndPersistFailureBillAudit() throws IOException {
        String mobileNumber = "1234567890";
        Money amountToCharge = new Money(0.60);
        Double currentBalance = 10D;
        IProgramType programType = getProgramType("Pregnancy");

        when(mtnBillingSystemMock.isMtnCustomer(mobileNumber)).thenReturn(false);

        BillingServiceResponse billingServiceResponse = service.hasAvailableFundForProgram(new BillingServiceRequest(mobileNumber, getProgramType("Pregnancy")));

        assertFalse(billingServiceResponse.isValid());
        assertEquals(billingServiceResponse.getValidationErrors(), Arrays.asList(ValidationError.NOT_A_VALID_CUSTOMER));
        verify(mtnBillingSystemMock).isMtnCustomer(mobileNumber);
        verify(mtnBillingSystemMock, never()).chargeCustomer(mobileNumber, amountToCharge.getValue());
        verify(allBillAccounts, never()).updateBillAccount(mobileNumber, currentBalance, programType);
        verify(allBillAudits).add(argThat(new BillAuditMatcher(new BillAudit(mobileNumber, amountToCharge.getValue(), BillStatus.FAILURE, ValidationError.NOT_A_VALID_CUSTOMER.name(), DateUtil.today()))));
    }

    @Test
    public void ShouldNotProceedForMtnCustomerWithInsufficientBalanceAndPersistFailureBillAudit() throws IOException {
        String mobileNumber = "1234567890";
        Money amountToCharge = new Money(0.60);
        Double currentBalance = 2D;

        when(mtnBillingSystemMock.isMtnCustomer(mobileNumber)).thenReturn(true);
        when(mtnBillingSystemMock.getAvailableBalance(mobileNumber)).thenReturn(0D);

        BillingServiceResponse billingServiceResponse = service.hasAvailableFundForProgram(new BillingServiceRequest(mobileNumber, getProgramType("Pregnancy")));

        assertFalse(billingServiceResponse.isValid());
        assertEquals(billingServiceResponse.getValidationErrors(), Arrays.asList(ValidationError.INSUFFICIENT_FUND));

        verify(mtnBillingSystemMock).isMtnCustomer(mobileNumber);
        verify(mtnBillingSystemMock).getAvailableBalance(mobileNumber);
        verify(mtnBillingSystemMock, never()).chargeCustomer(mobileNumber, amountToCharge.getValue());
        verify(allBillAccounts, never()).updateBillAccount(mobileNumber, currentBalance, getProgramType("Pregnancy"));
        verify(allBillAudits).add(argThat(new BillAuditMatcher(new BillAudit(mobileNumber, amountToCharge.getValue(), BillStatus.FAILURE, ValidationError.INSUFFICIENT_FUND.name(), DateUtil.today()))));
    }

    @Test
    public void ShouldChargeMtnCustomerAndGiveAValidResponseForCustomerWithValidFundsAndPersistSuccessBillAudit() throws IOException {
        String mobileNumber = "1234567890";
        Money amountToCharge = new Money(0.60);
        Double currentBalance = 2D;

        when(mtnBillingSystemMock.isMtnCustomer(mobileNumber)).thenReturn(true);
        when(mtnBillingSystemMock.getAvailableBalance(mobileNumber)).thenReturn(2D);

        IProgramType programType = getProgramType("Pregnancy");
        BillingServiceResponse billingServiceResponse = service.chargeSubscriptionFee(new BillingServiceRequest(mobileNumber, programType));

        assertTrue(billingServiceResponse.isValid());
        assertTrue(billingServiceResponse.getValidationErrors().isEmpty());

        verify(mtnBillingSystemMock).chargeCustomer(mobileNumber, amountToCharge.getValue());
        verify(allBillAccounts).updateBillAccount(mobileNumber, currentBalance, programType);
        verify(allBillAudits).add(argThat(new BillAuditMatcher(new BillAudit(mobileNumber, amountToCharge.getValue(), BillStatus.SUCCESS, StringUtils.EMPTY, DateUtil.today()))));
    }

    @Test
    public void ShouldDeductAmountForSubscriberAndCreateABillingSchedule() {

        service = spy(service);
        String mobileNumber = "94033312234";
        IProgramType programType = getProgramType("Child Care");
        LocalDate cycleStartDate = date(2011, 10, 7);
        RegistrationBillingRequest registrationBillingRequest = new RegistrationBillingRequest(mobileNumber, programType, cycleStartDate);

        doReturn(new BillingServiceResponse<String>()).when(service).chargeSubscriptionFee(registrationBillingRequest);

        BillingServiceResponse response = service.processRegistration(registrationBillingRequest);

        verify(service).chargeSubscriptionFee(registrationBillingRequest);
        assertTrue(response.isValid());

        ArgumentCaptor<CronSchedulableJob> captor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService).scheduleJob(captor.capture());
        CronSchedulableJob actualArgument = captor.getValue();
        MotechEvent event = actualArgument.getMotechEvent();
        Date expectedBillingDate = cycleStartDate.monthOfYear().addToCopy(1).toDate();

        assertEquals(BillingServiceImpl.BILLING_SCHEDULE_EVERY_MONTH, event.getSubject());
        Assert.assertEquals(BillingServiceImpl.BILLING_SCHEDULE_EVERY_MONTH + "." + programType.getProgramName() + "." + mobileNumber,
                event.getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        Assert.assertEquals(programType.getProgramName(), event.getParameters().get(PROGRAM));
        Assert.assertEquals(mobileNumber, event.getParameters().get(EXTERNAL_ID_KEY));
        Assert.assertEquals(expectedBillingDate, actualArgument.getStartTime());
        Assert.assertEquals("0 0 5 7 * ?", actualArgument.getCronExpression());
    }

    @Test
    public void ShouldNotCreateABillingSchedule_IfBillingCustomerIsNotSuccessful() {

        service = spy(service);
        String mobileNumber = "94033312234";
        IProgramType programType = getProgramType("Child Care");
        LocalDate cycleStartDate = date(2011, 10, 7);
        RegistrationBillingRequest registrationBillingRequest = new RegistrationBillingRequest(mobileNumber, programType, cycleStartDate);

        BillingServiceResponse<String> errorResponse = mock(BillingServiceResponse.class);
        when(errorResponse.isValid()).thenReturn(false);
        doReturn(errorResponse).when(service).chargeSubscriptionFee(registrationBillingRequest);

        BillingServiceResponse response = service.processRegistration(registrationBillingRequest);

        assertFalse(response.isValid());
        assertEquals(errorResponse, response);
        verify(schedulerService, never()).scheduleJob(Matchers.<CronSchedulableJob>any());
    }

    private LocalDate date(int year, int month , int day) {
        return new DateTime(year, month, day, 0, 0).toLocalDate();
    }

    private IProgramType getProgramType(final String programName) {
        return new IProgramType() {
            @Override
            public String getProgramName() {
                return programName;
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
            public Money getFee() {
                return new Money(0.6D);
            }
        };
    }
}