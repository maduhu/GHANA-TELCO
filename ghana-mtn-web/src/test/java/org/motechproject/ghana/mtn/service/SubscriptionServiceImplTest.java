package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.process.SubscriptionBilling;
import org.motechproject.ghana.mtn.process.SubscriptionCampaign;
import org.motechproject.ghana.mtn.process.SubscriptionPersistence;
import org.motechproject.ghana.mtn.process.SubscriptionValidation;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionServiceImplTest {

    private SubscriptionServiceImpl service;
    @Mock
    private SubscriptionValidation validation;
    @Mock
    private SubscriptionBilling billing;
    @Mock
    private SubscriptionPersistence persistence;
    @Mock
    private SubscriptionCampaign campaign;

    @Before
    public void setUp() {
        initMocks(this);
        service = new SubscriptionServiceImpl(validation, billing, persistence, campaign);
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

}
