package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.service.process.*;

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

        when(billing.endFor(subscription)).thenReturn(true);
        when(campaign.endFor(subscription)).thenReturn(true);
        when(persistence.endFor(subscription)).thenReturn(true);

        service.stop(subscription);

        verify(billing).endFor(subscription);
        verify(campaign).endFor(subscription);
        verify(persistence).endFor(subscription);
    }

}
