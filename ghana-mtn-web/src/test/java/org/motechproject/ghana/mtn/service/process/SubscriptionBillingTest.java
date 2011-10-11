package org.motechproject.ghana.mtn.service.process;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionBillingTest {
    private SubscriptionBilling billing;
    @Mock
    private BillingService billingService;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;

    @Before
    public void setUp() {
        initMocks(this);
        billing = new SubscriptionBilling(billingService, smsService, messageBundle);
    }

    @Test
    public void shouldReturnFalseInCaseOfValidationErrorsOnStartingCycle() {
        DateTime now = DateTime.now();
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
        DateTime now = DateTime.now();
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
        DateTime now = DateTime.now();
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

        Boolean reply = billing.endFor(subscription);

        assertFalse(reply);
        assertSMSRequest(mobileNumber, "errors message", program);
    }


    @Test
    public void shouldReturnTrueAndSendBillingSuccessMessageOnStoppingCycle() {
        DateTime now = DateTime.now();
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
        when(messageBundle.get(MessageBundle.BILLING_STOPPED)).thenReturn("billing stopped");
        when(billingService.stopBilling(any(BillingCycleRequest.class))).thenReturn(response);

        Boolean reply = billing.endFor(subscription);

        assertTrue(reply);
        assertSMSRequest(mobileNumber, "billing stopped", program);
    }

    private void assertSMSRequest(String mobileNumber, String errorMsg, String program) {
        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();

        assertEquals(errorMsg, captured.getMessage());
        assertEquals(mobileNumber, captured.getMobileNumber());
        assertEquals(program, captured.programName());
    }

    private void setupMocks(DateTime now, String mobileNumber, String program, ProgramType programType, Subscription subscription) {
        when(programType.getProgramName()).thenReturn(program);
        when(subscription.subscriberNumber()).thenReturn(mobileNumber);
        when(subscription.billingStartDate()).thenReturn(now);
        when(subscription.getProgramType()).thenReturn(programType);
    }


}
