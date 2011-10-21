package org.motechproject.ghana.mtn.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.TestData;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

public class PersistenceProcessorTest {
    private PersistenceProcess persistence;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;
    @Mock
    private AllSubscribers allSubscribers;
    @Mock
    private AllSubscriptions allSubscriptions;

    public final ProgramType childCareProgramType = TestData.childProgramType().build();

    @Before
    public void setUp() {
        initMocks(this);
        persistence = new PersistenceProcess(allSubscribers, allSubscriptions, smsService, messageBundle);
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
        persistence.stopExpired(subscription);

        verify(allSubscriptions).update(subscription);
        verify(subscription).setStatus(SubscriptionStatus.EXPIRED);
    }

    @Test
    public void shouldUpdateSubscriptionStateAsSuspendedAndSaveItOnEnding() {
        Subscription subscription = mock(Subscription.class);
        persistence.stopByUser(subscription);

        verify(allSubscriptions).update(subscription);
        verify(subscription).setStatus(SubscriptionStatus.SUSPENDED);
    }

    @Test
    public void shouldUpdateSubscriptionStateOfSourceAndTargetSubscription() {
        Subscription source = mock(Subscription.class);
        Subscription target = mock(Subscription.class);
        when(source.isPaymentDefaulted()).thenReturn(false);

        Boolean reply = persistence.rollOver(source, target);

        assertTrue(reply);
        verify(source).setStatus(SubscriptionStatus.ROLLED_OFF);
        verify(target).setStatus(SubscriptionStatus.ACTIVE);
        verify(target).updateStartCycleInfo();

        verify(allSubscriptions).update(source);
        verify(allSubscriptions).add(target);
    }

    @Test
    public void shouldUpdateSubscriptionStateToWaitingForResponseAndTargetSubscriptionShouldNotBeSaved() {
        Subscription source = new SubscriptionBuilder().withType(childCareProgramType).withStatus(WAITING_FOR_ROLLOVER_RESPONSE).build();
        Subscription target = mock(Subscription.class);

        Boolean reply = persistence.rollOver(source, target);

        assertTrue(reply);
        verify(target, never()).updateStartCycleInfo();
        assertThat(source.getStatus(), is(WAITING_FOR_ROLLOVER_RESPONSE));
        verify(allSubscriptions).update(source);
        verify(allSubscriptions, never()).add(target);
    }
}
