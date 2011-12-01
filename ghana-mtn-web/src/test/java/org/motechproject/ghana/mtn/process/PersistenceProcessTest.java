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
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.EXPIRED;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

public class PersistenceProcessTest {
    private PersistenceProcess persistence;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;
    @Mock
    private AllSubscribers allSubscribers;
    @Mock
    private AllSubscriptions allSubscriptions;

    public final ProgramType pregnancyProgramType = TestData.pregnancyProgramType().build();

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
    }

    @Test
    public void shouldUpdateSubscriptionStateAndSaveItOnEnding() {
        Subscription subscription = mock(Subscription.class);
        persistence.stopExpired(subscription);

        verify(allSubscriptions).update(subscription);
        verify(subscription).setStatus(EXPIRED);
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

        Boolean reply = persistence.rollOver(source, target);

        assertTrue(reply);
        verify(source).setStatus(SubscriptionStatus.ROLLED_OFF);
        verify(target).setStatus(SubscriptionStatus.ACTIVE);

        verify(allSubscriptions).update(source);
        verify(allSubscriptions).add(target);
    }

    @Test
    public void shouldUpdateSubscriptionStateToWaitingForResponseAndTargetSubscriptionShouldNotBeSaved() {
        Subscription source = new SubscriptionBuilder().withType(pregnancyProgramType).withStatus(WAITING_FOR_ROLLOVER_RESPONSE).build();
        Subscription target = mock(Subscription.class);

        Boolean reply = persistence.rollOver(source, target);

        assertTrue(reply);
        assertThat(source.getStatus(), is(WAITING_FOR_ROLLOVER_RESPONSE));
        verify(allSubscriptions).update(source);
        verify(allSubscriptions, never()).add(target);
    }
         
    @Test
    public void shouldUpdateSubscriptionOfPregnancyToExpiredStatus_IfUserWantsToRetainExistingChildCareProgram() {
        Subscription pregnancySubscriptionWaitingForRollOver = new SubscriptionBuilder().withType(pregnancyProgramType).withStatus(WAITING_FOR_ROLLOVER_RESPONSE).build();
        Subscription childCare = mock(Subscription.class);

        Boolean reply = persistence.retainExistingChildCare(pregnancySubscriptionWaitingForRollOver, childCare);
        assertTrue(reply);
        assertThat(pregnancySubscriptionWaitingForRollOver.getStatus(), is(EXPIRED));
        verify(allSubscriptions).update(pregnancySubscriptionWaitingForRollOver);
        verifyNoMoreInteractions(allSubscriptions);
    }
    
    @Test
    public void shouldUpdateExistingChildCareSubscriptionToExpiredStatusAndUpdateRollOverPregnancySubscription_IfUserWantsToStopTheExistingChildCareProgram() {
        Subscription pregnancySubscriptionWaitingForRollOver = mock(Subscription.class);
        Subscription newChildCareSubscriptionToRollOver = mock(Subscription.class);
        Subscription existingChildCareSubscrition = mock(Subscription.class);

        Boolean reply = persistence.rollOverToNewChildCareProgram(pregnancySubscriptionWaitingForRollOver, newChildCareSubscriptionToRollOver, existingChildCareSubscrition);

        assertTrue(reply);
        verify(pregnancySubscriptionWaitingForRollOver).setStatus(SubscriptionStatus.ROLLED_OFF);
        verify(newChildCareSubscriptionToRollOver).setStatus(SubscriptionStatus.ACTIVE);
        verify(existingChildCareSubscrition).setStatus(SubscriptionStatus.EXPIRED);

        verify(allSubscriptions).update(pregnancySubscriptionWaitingForRollOver);
        verify(allSubscriptions).add(newChildCareSubscriptionToRollOver);
        verify(allSubscriptions).update(existingChildCareSubscrition);
    }
}
