package org.motechproject.ghana.mtn.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.process.BillingServiceMediator;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.*;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.PAYMENT_DEFAULT;

public class DefaultedBillingEventHandlerTest {

    DefaultedBillingEventHandler handler;
    @Mock
    private BillingServiceMediator billingServiceMediator;
    @Mock
    private AllSubscriptions allSubscriptions;

    @Before
    public void setup() {
        initMocks(this);
        handler = new DefaultedBillingEventHandler(allSubscriptions, billingServiceMediator);
    }

    @Test
    public void shouldCallBillingServiceToChargeDefaultCustomerForCheckDailyAndCheckWeekly() {

        Map<String, Object> params = new HashMap<String, Object>();
        String programKey = "programkey";
        String externalId = "externalid";
        params.put(PROGRAM_KEY, programKey);
        params.put(EXTERNAL_ID_KEY, externalId);
        MotechEvent event = new MotechEvent(DEFAULTED_DAILY_SCHEDULE, params);

        Subscription subscription = new SubscriptionBuilder().build();
        when(allSubscriptions.findBy(externalId, programKey, PAYMENT_DEFAULT)).thenReturn(subscription);

        handler.checkDaily(event);
        verify(billingServiceMediator).chargeFeeForDefaultedSubscriptionDaily(subscription);

        reset(billingServiceMediator);

        handler.checkWeekly(event);
        verify(billingServiceMediator).chargeFeeForDefaultedSubscriptionWeekly(subscription);
    }

    @Test
    public void shouldNotCallBillingServiceToChargeDefaultCustomerForCheckDailyAndCheckWeekly_IfSubscriptionIsNotPaymentDefault() {

        Map<String, Object> params = new HashMap<String, Object>();
        String programKey = "programkey";
        String externalId = "externalid";
        params.put(PROGRAM_KEY, programKey);
        params.put(EXTERNAL_ID_KEY, externalId);
        MotechEvent event = new MotechEvent(DEFAULTED_DAILY_SCHEDULE, params);

        when(allSubscriptions.findBy(externalId, programKey, PAYMENT_DEFAULT)).thenReturn(null);

        handler.checkDaily(event);
        verify(billingServiceMediator, never()).chargeFeeForDefaultedSubscriptionDaily(any(Subscription.class));

        reset(billingServiceMediator);

        handler.checkWeekly(event);
        verify(billingServiceMediator, never()).chargeFeeForDefaultedSubscriptionWeekly(any(Subscription.class));
    }
}
