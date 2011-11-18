package org.motechproject.ghana.mtn.process;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillingCycleProcessorTest {
    private BillingCycleProcess billing;
    @Mock
    private BillingService billingService;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;

    public final ProgramType childCarePregnancyType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(1).withMaxWeek(52).withProgramName("Child Care").withShortCode("C").withShortCode("c").build();
    public final ProgramType pregnancyProgramType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(5).withMaxWeek(35).withProgramName("Pregnancy").withShortCode("P").withShortCode("p").build();

    @Before
    public void setUp() {
        initMocks(this);
        billing = new BillingCycleProcess(billingService, smsService, messageBundle);
    }

    @Test
    public void shouldReturnFalseInCaseOfValidationErrorsOnStartingCycle() {
        DateTime now = DateUtil.now();
        String mobileNumber = "123";
        String program = "program";

        ProgramType programType = mock(ProgramType.class);
        Subscription subscription = mock(Subscription.class);
        BillingServiceResponse response = mock(BillingServiceResponse.class);
        List errors = new ArrayList<ValidationError>();

        setupMocks(now, mobileNumber, program, programType, subscription);

        when(response.hasErrors()).thenReturn(true);
        when(response.getValidationErrors()).thenReturn(errors);
        when(messageBundle.get(errors)).thenReturn("errors message");
        when(billingService.startBilling(any(BillingCycleRequest.class))).thenReturn(response);

        Boolean reply = billing.startFor(subscription);

        assertFalse(reply);
        assertSMSRequest(mobileNumber, "errors message", program);
    }

    @Test
    public void shouldReturnTrueAndSendBillingSuccessMessageOnStartingCycle() {
        DateTime now = DateUtil.now();
        String mobileNumber = "123";
        String program = "program";

        ProgramType programType = mock(ProgramType.class);
        Subscription subscription = mock(Subscription.class);
        BillingServiceResponse response = mock(BillingServiceResponse.class);
        CustomerBill customerBill = mock(CustomerBill.class);

        setupMocks(now, mobileNumber, program, programType, subscription);

        when(customerBill.amountCharged()).thenReturn(new Double(12));
        when(response.hasErrors()).thenReturn(false);
        when(response.getValue()).thenReturn(customerBill);
        when(messageBundle.get(MessageBundle.BILLING_SUCCESS)).thenReturn("success message %s");
        when(billingService.startBilling(any(BillingCycleRequest.class))).thenReturn(response);

        Boolean reply = billing.startFor(subscription);

        assertTrue(reply);
        assertSMSRequest(mobileNumber, "success message 12.0", program);
    }


    @Test
    public void shouldReturnFalseInCaseOfValidationErrorsOnStoppingCycle() {
        DateTime now = DateUtil.now();
        String mobileNumber = "123";
        String program = "program";

        ProgramType programType = mock(ProgramType.class);
        Subscription subscription = mock(Subscription.class);
        BillingServiceResponse response = mock(BillingServiceResponse.class);
        List errors = new ArrayList<ValidationError>();

        setupMocks(now, mobileNumber, program, programType, subscription);

        when(response.hasErrors()).thenReturn(true);
        when(response.getValidationErrors()).thenReturn(errors);
        when(messageBundle.get(errors)).thenReturn("errors message");
        when(billingService.stopBilling(any(BillingCycleRequest.class))).thenReturn(response);

        Boolean reply = billing.stopExpired(subscription);

        assertFalse(reply);
        assertSMSRequest(mobileNumber, "errors message", program);
    }


    @Test
    public void shouldReturnTrueAndSendBillingSuccessMessageOnStoppingCycle() {
        DateTime now = DateUtil.now();
        String mobileNumber = "123";
        ProgramType programType = childCarePregnancyType;
        Subscription subscription = subscriptionBuilder(mobileNumber, now, now, programType)
                        .withType(programType).build();
        BillingServiceResponse successResponse = new BillingServiceResponse();

        when(messageBundle.get(MessageBundle.BILLING_STOPPED)).thenReturn("billing stopped");
        when(billingService.stopBilling(any(BillingCycleRequest.class))).thenReturn(successResponse);

        Boolean reply = billing.stopExpired(subscription);

        assertTrue(reply);
        assertSMSRequest(mobileNumber, "billing stopped", childCarePregnancyType.getProgramName());
        assertEquals(SubscriptionStatus.EXPIRED, subscription.getStatus());
    }

    @Test
    public void shouldReturnFalseAndSendMessageInCaseOfValidationErrorsOnStoppingCycleByUser() {
        DateTime now = DateUtil.now();
        String mobileNumber = "123";

        ProgramType programType = childCarePregnancyType;
        Subscription subscription = subscriptionBuilder(mobileNumber, now, now, programType)
                        .withType(programType).build();
        BillingServiceResponse response = mock(BillingServiceResponse.class);
        List errors = mockBillingServiceResponseWithErrors(response);
        when(messageBundle.get(errors)).thenReturn("errors message");
        when(billingService.stopBilling(any(BillingCycleRequest.class))).thenReturn(response);

        Boolean reply = billing.stopByUser(subscription);

        assertFalse(reply);
        assertSMSRequest(mobileNumber, "errors message", programType.getProgramName());
    }

    @Test
    public void shouldReturnTrueAndSendBillingSuccessMessageOnStoppingCycle_WhenUserWantsToStop() {
        DateTime now = DateUtil.now();
        String mobileNumber = "123";
        String program = "Child Care";
        ProgramType programType = childCarePregnancyType;
        Subscription subscription = subscriptionBuilder(mobileNumber, now, now, programType)
                        .withType(programType).build();
        BillingServiceResponse successResponse = new BillingServiceResponse();
        when(billingService.stopBilling(any(BillingCycleRequest.class))).thenReturn(successResponse);
        when(messageBundle.get(MessageBundle.BILLING_STOPPED)).thenReturn("billing stopped");

        Boolean reply = billing.stopByUser(subscription);

        assertTrue(reply);
        assertSMSRequest(mobileNumber, "billing stopped", program);
        assertEquals(SubscriptionStatus.SUSPENDED, subscription.getStatus());
    }

    @Test
    public void shouldStopSourceBillingCycleAndStartTargetBilling() {
        DateTime now = DateUtil.now();
        String mobileNumber = "123";
        String sourceProgram = "source_program";
        String targetProgram = "target_program";

        ProgramType sourceProgramType = mock(ProgramType.class);
        ProgramType targetProgramType = mock(ProgramType.class);
        Subscription sourceSubscription = mock(Subscription.class);
        Subscription targetSubscription = mock(Subscription.class);

        setupMocks(now, mobileNumber, sourceProgram, sourceProgramType, sourceSubscription);
        setupMocks(now, mobileNumber, targetProgram, targetProgramType, targetSubscription);

        CustomerBill sourceBill = mock(CustomerBill.class);
        CustomerBill targetBill = mock(CustomerBill.class);
        BillingServiceResponse sourceResponse = mock(BillingServiceResponse.class);
        BillingServiceResponse targetResponse = mock(BillingServiceResponse.class);
        when(sourceResponse.hasErrors()).thenReturn(false);
        when(targetResponse.hasErrors()).thenReturn(false);
        when(sourceResponse.getValue()).thenReturn(sourceBill);
        when(targetResponse.getValue()).thenReturn(targetBill);

        when(billingService.stopBilling(any(BillingCycleRequest.class))).thenReturn(sourceResponse);
        when(billingService.rollOverBilling(any(BillingCycleRequest.class))).thenReturn(targetResponse);

        when(messageBundle.get(MessageBundle.BILLING_STOPPED)).thenReturn("billing stopped");
        when(messageBundle.get(MessageBundle.BILLING_ROLLOVER)).thenReturn("billing rolled over");

        billing.rollOver(sourceSubscription, targetSubscription);

        ArgumentCaptor<BillingCycleRequest> captor = ArgumentCaptor.forClass(BillingCycleRequest.class);
        verify(billingService).rollOverBilling(captor.capture());
        BillingCycleRequest captured = captor.getValue();

        assertEquals(now, captured.getCycleStartDate());

    }

    private List mockBillingServiceResponseWithErrors(BillingServiceResponse response) {
        List errors = new ArrayList<ValidationError>();
        when(response.hasErrors()).thenReturn(true);
        when(response.getValidationErrors()).thenReturn(errors);
        return errors;
    }

    private void setupMocks(DateTime now, String mobileNumber, String program, ProgramType programType, Subscription subscription) {
        when(programType.getProgramName()).thenReturn(program);
        when(subscription.subscriberNumber()).thenReturn(mobileNumber);
        when(subscription.billingStartDate()).thenReturn(now);
        when(subscription.getProgramType()).thenReturn(programType);
    }

    private void assertSMSRequest(String mobileNumber, String errorMsg, String program) {
        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();

        assertEquals(errorMsg, captured.getMessage());
        assertEquals(mobileNumber, captured.getMobileNumber());
        assertEquals(program, captured.programName());
    }

    private SubscriptionBuilder subscriptionBuilder(String subscriberNumber, DateTime registrationDate, DateTime billingStartDate, ProgramType programType) {
        return new SubscriptionBuilder().withBillingStartDate(billingStartDate).withRegistrationDate(registrationDate)
                        .withSubscriber(new Subscriber(subscriberNumber))
                        .withType(programType);
    }

}
