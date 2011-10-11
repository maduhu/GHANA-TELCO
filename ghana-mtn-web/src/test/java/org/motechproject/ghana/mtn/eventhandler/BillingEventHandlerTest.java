package org.motechproject.ghana.mtn.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.service.BillingScheduler;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.sms.SMSService;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillingEventHandlerTest {

    BillingEventHandler eventHandler;
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private BillingService billingService;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;

    ProgramType childCareProgramType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(1).withMaxWeek(52)
            .withProgramName("Child Care").withShortCode("C").withShortCode("c").build();

    String successMsg = "Success message for billing - amount %s";
    @Before
    public void setUp() {
        initMocks(this);
        eventHandler = new BillingEventHandler(allSubscriptions, billingService, smsService, messageBundle);
        when(messageBundle.get(MessageBundle.BILLING_SUCCESS)).thenReturn(successMsg);
    }

    @Test
    public void shouldChargeCustomerForEveryMonthSchedule() {

        String subscriberNumber = "9500012345";
        String programName = "Child Care";

        Subscription subscription = mock(Subscription.class);
        BillingServiceResponse<CustomerBill> billingResponse = new BillingServiceResponse<CustomerBill>(new CustomerBill(successMsg, childCareProgramType.getFee()));
        when(subscription.getProgramType()).thenReturn(childCareProgramType);
        when(allSubscriptions.findBy(subscriberNumber, programName)).thenReturn(subscription);
        when(billingService.chargeProgramFee(any(BillingServiceRequest.class))).thenReturn(billingResponse);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(BillingScheduler.EXTERNAL_ID_KEY, subscriberNumber);
        params.put(BillingScheduler.PROGRAM, programName);
        MotechEvent event = new MotechEvent(BillingScheduler.MONTHLY_BILLING_SCHEDULE_SUBJECT, params);

        eventHandler.chargeCustomer(event);

        ArgumentCaptor<BillingServiceRequest> billingRequestCaptor = ArgumentCaptor.forClass(BillingServiceRequest.class);
        verify(billingService).chargeProgramFee(billingRequestCaptor.capture());
        BillingServiceRequest billingRequest = billingRequestCaptor.getValue();
        assertEquals(subscriberNumber, billingRequest.getMobileNumber());
        assertEquals(childCareProgramType.getFee(), billingRequest.getProgramFee());
        assertEquals(childCareProgramType, billingRequest.getProgramType());

        ArgumentCaptor<SMSServiceRequest> smsRequestCaptor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(smsRequestCaptor.capture());
        SMSServiceRequest request = smsRequestCaptor.getValue();
        assertEquals(format(successMsg, childCareProgramType.getFee()), request.getMessage());
        assertEquals(subscriberNumber, request.getMobileNumber());
        assertEquals(childCareProgramType, request.getProgramType());
    }
}
