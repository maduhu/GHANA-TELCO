package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionServiceRequest;
import org.motechproject.ghana.mtn.service.process.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionServiceImplTest {

    private SubscriptionServiceImpl service;
    @Mock
    private SubscriptionParser parser;
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
        service = new SubscriptionServiceImpl(parser, validation, billing, persistence, campaign);
    }

    @Test
    public void shouldCallAllProcessInvolvedOnNoErrorsDuringStartSubscription() {
        String inputMsg = "P 24";
        String mobileNumber = "123";
        SubscriptionServiceRequest request = mock(SubscriptionServiceRequest.class);
        Subscription subscription = mock(Subscription.class);

        when(request.getSubscriberNumber()).thenReturn(mobileNumber);
        when(request.getInputMessage()).thenReturn(inputMsg);
        when(parser.parseForEnrollment(mobileNumber, inputMsg)).thenReturn(subscription);
        when(validation.startFor(subscription)).thenReturn(true);
        when(billing.startFor(subscription)).thenReturn(true);
        when(persistence.startFor(subscription)).thenReturn(true);
        when(campaign.startFor(subscription)).thenReturn(true);

        service.startFor(request);

        ArgumentCaptor<Subscriber> captor = ArgumentCaptor.forClass(Subscriber.class);
        verify(subscription).setSubscriber(captor.capture());
        assertEquals(mobileNumber, captor.getValue().getNumber());

        verify(validation).startFor(subscription);
        verify(billing).startFor(subscription);
        verify(persistence).startFor(subscription);
        verify(campaign).startFor(subscription);
    }

}
