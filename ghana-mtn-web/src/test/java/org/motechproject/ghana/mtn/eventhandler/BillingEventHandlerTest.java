package org.motechproject.ghana.mtn.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.service.BillingScheduler;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.process.FeeChargerProcess;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
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
    @Mock
    private FeeChargerProcess feeCharger;

    @Before
    public void setUp() {
        initMocks(this);
        eventHandler = new BillingEventHandler(allSubscriptions, feeCharger);
    }

    @Test
    public void shouldChargeCustomerForEveryMonthSchedule() {
        String subscriberNumber = "9500012345";
        String programName = "Child Care";
        Subscription subscription = mock(Subscription.class);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(BillingScheduler.EXTERNAL_ID_KEY, subscriberNumber);
        params.put(BillingScheduler.PROGRAM_KEY, programName);
        MotechEvent event = new MotechEvent(BillingScheduler.MONTHLY_BILLING_SCHEDULE_SUBJECT, params);

        when(allSubscriptions.findBy(subscriberNumber, programName)).thenReturn(subscription);

        eventHandler.chargeCustomer(event);

        verify(feeCharger).process(subscription);
    }
}
