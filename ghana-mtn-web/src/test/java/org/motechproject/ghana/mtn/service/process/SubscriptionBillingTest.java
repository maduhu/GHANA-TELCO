package org.motechproject.ghana.mtn.service.process;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
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
    public void shouldReturnFalseInCaseOfValidationErrors() {
        DateTime now = DateTime.now();
        String mobileNumber = "123";
        String errorMsg = "errors message";
        String program = "program";
        ProgramType programType = mock(ProgramType.class);
        Subscription subscription = mock(Subscription.class);
        List errors = new ArrayList<ValidationError>();
        BillingServiceResponse response = mock(BillingServiceResponse.class);

        when(programType.getProgramName()).thenReturn(program);
        when(subscription.subscriberNumber()).thenReturn(mobileNumber);
        when(subscription.billingStartDate()).thenReturn(now);
        when(subscription.getProgramType()).thenReturn(programType);
        when(response.hasErrors()).thenReturn(true);
        when(response.getValidationErrors()).thenReturn(errors);
        when(messageBundle.get(errors)).thenReturn(errorMsg);
        when(billingService.startBilling(any(BillingCycleRequest.class))).thenReturn(response);

        billing.startFor(subscription);

        ArgumentCaptor<SMSServiceRequest>  captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();
        assertEquals(errorMsg, captured.getMessage());
        assertEquals(mobileNumber, captured.getMobileNumber());
        assertEquals(program, captured.programName());

    }


}
