package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.process.SubscriptionBillingCycle;
import org.motechproject.ghana.mtn.process.SubscriptionCampaign;
import org.motechproject.ghana.mtn.process.SubscriptionPersistence;
import org.motechproject.ghana.mtn.process.SubscriptionValidation;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.vo.Money;

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

    public final ProgramType childCarePregnancyType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(1).withMaxWeek(52).withProgramName("Child Care").withShortCode("C").withShortCode("c").build();

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

        when(billing.stopExpired(subscription)).thenReturn(true);
        when(campaign.stopExpired(subscription)).thenReturn(true);
        when(persistence.stopExpired(subscription)).thenReturn(true);

        service.stopExpired(subscription);

        verify(billing).stopExpired(subscription);
        verify(campaign).stopExpired(subscription);
        verify(persistence).stopExpired(subscription);
    }

    @Test
    public void shouldInvokeAllProcessInvolvedInRollOverProcess() {
        Subscription source = mock(Subscription.class);
        Subscription target = mock(Subscription.class);

        when(validation.rollOver(source,target)).thenReturn(true);
        when(billing.rollOver(source,target)).thenReturn(true);
        when(campaign.rollOver(source,target)).thenReturn(true);
        when(persistence.rollOver(source,target)).thenReturn(true);

        service.rollOver(source, target);

        verify(validation).rollOver(source,target);
        verify(billing).rollOver(source,target);
        verify(campaign).rollOver(source,target);
        verify(persistence).rollOver(source,target);

    }

    @Test
    public void shouldInvokeAllProcessInvolvedInStopProcessByUser() {
        String subscriberNumber = "9500012345";
        IProgramType programType = childCarePregnancyType;
        Subscription subscription = mock(Subscription.class);

        when(validation.validateSubscriptionToStop(subscriberNumber, programType)).thenReturn(subscription);
        when(billing.stopByUser(subscription)).thenReturn(true);
        when(campaign.stopByUser(subscription)).thenReturn(true);
        when(persistence.stopByUser(subscription)).thenReturn(true);

        service.stopByUser(subscriberNumber, programType);

        verify(validation).validateSubscriptionToStop(subscriberNumber, programType);
        verify(billing).stopByUser(subscription);
        verify(campaign).stopByUser(subscription);
        verify(persistence).stopByUser(subscription);

    }

    @Test
    public void shouldNotInvokeAllStopProcessByUserIfValidationFailsInFindingSubscriptionToStop() {
        String subscriberNumber = "9500012345";
        IProgramType programType = childCarePregnancyType;
        Subscription subscription = mock(Subscription.class);

        when(validation.validateSubscriptionToStop(subscriberNumber, programType)).thenReturn(null);
        when(billing.stopByUser(subscription)).thenReturn(true);
        when(campaign.stopByUser(subscription)).thenReturn(true);
        when(persistence.stopByUser(subscription)).thenReturn(true);

        service.stopByUser(subscriberNumber, programType);

        verify(validation).validateSubscriptionToStop(subscriberNumber, programType);
        verify(billing, never()).stopByUser(subscription);
        verify(campaign, never()).stopByUser(subscription);
        verify(persistence, never()).stopByUser(subscription);
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

    @Test
    public void shouldGetAllActiveSubscriptions() {
        String subscriberNumber = "9844321234";
        service.activeSubscriptions(subscriberNumber);
        verify(allSubscriptions).getAllActiveSubscriptionsForSubscriber(subscriberNumber);
    }

}
