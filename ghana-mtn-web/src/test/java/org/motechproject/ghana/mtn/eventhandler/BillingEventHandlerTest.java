package org.motechproject.ghana.mtn.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.service.BillingScheduler;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.process.BillingServiceMediator;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.EXTERNAL_ID_KEY;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.PROGRAM_KEY;
import static org.motechproject.ghana.mtn.domain.IProgramType.PREGNANCY;

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
    @Mock
    private BillingServiceMediator feeCharger;

    @Before
    public void setUp() {
        initMocks(this);
        eventHandler = new BillingEventHandler(allSubscriptions, feeCharger);
    }

    @Test
    public void shouldChargeCustomerForEveryMonthSchedule() {
        String subscriberNumber = "9500012345";
        String programKey = PREGNANCY;
        Subscription subscription = mock(Subscription.class);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EXTERNAL_ID_KEY, subscriberNumber);
        params.put(PROGRAM_KEY, programKey);
        MotechEvent event = new MotechEvent(BillingScheduler.MONTHLY_BILLING_SCHEDULE_SUBJECT, params);

        when(allSubscriptions.findActiveSubscriptionFor(subscriberNumber, programKey)).thenReturn(subscription);

        eventHandler.chargeCustomer(event);

        verify(feeCharger).chargeFeeAndHandleResponse(subscription);
    }
}
