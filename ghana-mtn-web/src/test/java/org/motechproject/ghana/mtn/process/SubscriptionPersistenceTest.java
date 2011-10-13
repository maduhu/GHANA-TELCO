package org.motechproject.ghana.mtn.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;

import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class SubscriptionPersistenceTest {
    private SubscriptionPersistence persistence;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;
    @Mock
    private AllSubscribers allSubscribers;
    @Mock
    private AllSubscriptions allSubscriptions;

    @Before
    public void setUp() {
        initMocks(this);
        persistence = new SubscriptionPersistence(allSubscribers, allSubscriptions, smsService, messageBundle);
    }

    @Test
    public void shouldUpdateSubscriptionStateAndSaveItAndSubscriber() {
        Subscriber subscriber = new Subscriber();
        Subscription subscription = mock(Subscription.class);

        when(subscription.getSubscriber()).thenReturn(subscriber);

        persistence.startFor(subscription);

        verify(allSubscribers).add(subscriber);
        verify(allSubscriptions).add(subscription);
        verify(subscription).setStatus(SubscriptionStatus.ACTIVE);
        verify(subscription).updateStartCycleInfo();
    }

    @Test
    public void shouldUpdateSubscriptionStateAndSaveItOnEnding() {
        Subscription subscription = mock(Subscription.class);
        persistence.stopFor(subscription);

        verify(allSubscriptions).update(subscription);
        verify(subscription).setStatus(SubscriptionStatus.EXPIRED);
    }

    @Test
    public void shouldUpdateSubscriptionStateOfSourceAndTargetSubscription(){
        Subscription source = mock(Subscription.class);
        Subscription target = mock(Subscription.class);

        Boolean reply = persistence.rollOver(source, target);

        assertTrue(reply);
        verify(source).setStatus(SubscriptionStatus.ROLLED_OFF);
        verify(target).setStatus(SubscriptionStatus.ACTIVE);
        verify(allSubscriptions).update(source);
        verify(allSubscriptions).update(target);

    }
}
