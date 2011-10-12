package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.process.SubscriptionBillingCycle;
import org.motechproject.ghana.mtn.process.SubscriptionCampaign;
import org.motechproject.ghana.mtn.process.SubscriptionPersistence;
import org.motechproject.ghana.mtn.process.SubscriptionValidation;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionServiceImplTest {

    private SubscriptionServiceImpl service;
    @Mock
    private SubscriptionValidation validation;
    @Mock
    private SubscriptionBillingCycle billing;
    @Mock
    private SubscriptionPersistence persistence;
    @Mock
    private SubscriptionCampaign campaign;
    @Mock
    private AllSubscriptions allSubscriptions;

    @Before
    public void setUp() {
        initMocks(this);
        service = new SubscriptionServiceImpl(allSubscriptions, validation, billing, persistence, campaign);
    }

    @Test
    public void shouldCallAllProcessInvolvedOnNoErrorsDuringStartSubscription() {
        Subscription subscription = mock(Subscription.class);

        when(validation.startFor(subscription)).thenReturn(true);
        when(billing.startFor(subscription)).thenReturn(true);
        when(persistence.startFor(subscription)).thenReturn(true);
        when(campaign.startFor(subscription)).thenReturn(true);

        service.start(subscription);

        verify(validation).startFor(subscription);
        verify(billing).startFor(subscription);
        verify(persistence).startFor(subscription);
        verify(campaign).startFor(subscription);
    }

    @Test
    public void shouldCallAllProcessInvolvedOnNoErrorsDuringStopSubscription() {
        Subscription subscription = mock(Subscription.class);

        when(billing.stopFor(subscription)).thenReturn(true);
        when(campaign.stopFor(subscription)).thenReturn(true);
        when(persistence.stopFor(subscription)).thenReturn(true);

        service.stop(subscription);

        verify(billing).stopFor(subscription);
        verify(campaign).stopFor(subscription);
        verify(persistence).stopFor(subscription);
    }

    @Test
    public void shouldFindSubscriptionByMobileNumberUsingRepository() {
        String subscriberNumber = "123";
        String program = "program";
        Subscription subscription = new Subscription();
        when(allSubscriptions.findBy(subscriberNumber, program)).thenReturn(subscription);

        Subscription returned = service.findBy(subscriberNumber, program);

        assertEquals(subscription, returned);
        verify(allSubscriptions).findBy(subscriberNumber, program);
    }

}
