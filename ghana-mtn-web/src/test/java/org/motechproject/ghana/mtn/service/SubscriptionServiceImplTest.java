package org.motechproject.ghana.mtn.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.TestData;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.process.CampaignProcess;
import org.motechproject.ghana.mtn.process.PersistenceProcess;
import org.motechproject.ghana.mtn.process.ValidationProcess;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.domain.ProgramType.CHILDCARE;
import static org.motechproject.ghana.mtn.domain.ProgramType.PREGNANCY;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.ACTIVE;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

public class SubscriptionServiceImplTest {

    private SubscriptionServiceImpl service;
    @Mock
    private ValidationProcess validation;
    @Mock
    private PersistenceProcess persistence;
    @Mock
    private CampaignProcess campaign;
    @Mock
    private AllSubscriptions allSubscriptions;

    public final ProgramType childCareProgramType = TestData.childProgramType().build();
    public final ProgramType pregnancyProgramType = TestData.pregnancyProgramType().withRollOverProgramType(childCareProgramType).build();

    @Before
    public void setUp() {
        initMocks(this);
        service = new SubscriptionServiceImpl(allSubscriptions, validation, persistence, campaign);
    }

    @Test
    public void shouldCallAllProcessInvolvedOnNoErrorsDuringStartSubscription() {
        Subscription subscription = mock(Subscription.class);

        when(validation.startFor(subscription)).thenReturn(true);
        when(persistence.startFor(subscription)).thenReturn(true);
        when(campaign.startFor(subscription)).thenReturn(true);

        service.start(subscription);

        verify(subscription).updateCycleInfo();
        verify(validation).startFor(subscription);
        verify(persistence).startFor(subscription);
        verify(campaign).startFor(subscription);
    }

    @Test
    public void shouldCallAllProcessInvolvedOnNoErrorsDuringStopSubscription() {
        Subscription subscription = mock(Subscription.class);

        when(campaign.stopExpired(subscription)).thenReturn(true);
        when(persistence.stopExpired(subscription)).thenReturn(true);

        service.stopExpired(subscription);
        verify(campaign).stopExpired(subscription);
        verify(persistence).stopExpired(subscription);
    }

    @Test
    public void shouldInvokeAllProcessInvolvedInRollOverProcessForEvent() {
        Subscription subscription = spy(new SubscriptionBuilder().withRegistrationDate(DateUtil.now())
                .withSubscriber(new Subscriber("9850012345")).withType(pregnancyProgramType).withStartWeekAndDay(new WeekAndDay(new Week(36), DayOfWeek.Friday))
                .build().updateCycleInfo());
        when(subscription.isCompleted()).thenReturn(true);
        when(validation.rollOver(eq(subscription), Matchers.<Subscription>any())).thenReturn(true);
        when(campaign.rollOver(eq(subscription), Matchers.<Subscription>any())).thenReturn(true);
        when(persistence.rollOver(eq(subscription), Matchers.<Subscription>any())).thenReturn(true);

        service.rollOverByEvent(subscription);

        verify(validation).rollOver(eq(subscription), Matchers.<Subscription>any());
        verify(campaign).rollOver(eq(subscription), Matchers.<Subscription>any());
        verify(persistence).rollOver(eq(subscription), Matchers.<Subscription>any());
    }

    @Test
    public void shouldNotInvokeRollOverIfValidationIsNotSuccessful(){
        Date deliveryDate = null;
        String subscriberNumber = "1234567890";

        when(validation.validateForRollOver(subscriberNumber, deliveryDate)).thenReturn(null);

        service = spy(service);
        service.rollOver(subscriberNumber, deliveryDate);

        verify(validation, never()).rollOver(Matchers.<Subscription>any(), Matchers.<Subscription>any());
        verify(campaign, never()).rollOver(Matchers.<Subscription>any(), Matchers.<Subscription>any());
        verify(persistence, never()).rollOver(Matchers.<Subscription>any(), Matchers.<Subscription>any());
    }

    @Test
    public void shouldInvokeRollOverIfValidationIsSuccessful(){
        Date deliveryDate = null;
        String subscriberNumber = "1234567890";

        DateTime registrationDate = DateUtil.newDate(2011, 10, 21).toDateTimeAtCurrentTime();
        Subscription subscription = new SubscriptionBuilder().withSubscriber(new Subscriber(subscriberNumber)).withType(pregnancyProgramType)
                .withRegistrationDate(registrationDate).build();
        when(validation.validateForRollOver(subscriberNumber, deliveryDate)).thenReturn(subscription);

        when(validation.rollOver(eq(subscription), Matchers.<Subscription>any())).thenReturn(true);
        when(campaign.rollOver(eq(subscription), Matchers.<Subscription>any())).thenReturn(true);
        when(persistence.rollOver(eq(subscription), Matchers.<Subscription>any())).thenReturn(true);

        service = spy(service);
        service.rollOver(subscriberNumber, deliveryDate);

        ArgumentCaptor<Subscription> childCareCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(validation).rollOver(eq(subscription), childCareCaptor.capture());

        verify(campaign).rollOver(eq(subscription), Matchers.<Subscription>any());
        verify(persistence).rollOver(eq(subscription), Matchers.<Subscription>any());
    }

    @Test
    public void shouldInvokeAllProcessInvolvedInStopProcessByUser() {
        String subscriberNumber = "9500012345";
        ProgramType programType = childCareProgramType;
        Subscription subscription = mock(Subscription.class);

        when(validation.validateSubscriptionToStop(subscriberNumber, programType)).thenReturn(subscription);
        when(campaign.stopByUser(subscription)).thenReturn(true);
        when(persistence.stopByUser(subscription)).thenReturn(true);

        service.stopByUser(subscriberNumber, programType);

        verify(validation).validateSubscriptionToStop(subscriberNumber, programType);
        verify(campaign).stopByUser(subscription);
        verify(persistence).stopByUser(subscription);

    }

    @Test
    public void shouldNotInvokeAllStopProcessByUserIfValidationFailsInFindingSubscriptionToStop() {
        String subscriberNumber = "9500012345";
        ProgramType programType = childCareProgramType;
        Subscription subscription = mock(Subscription.class);

        when(validation.validateSubscriptionToStop(subscriberNumber, programType)).thenReturn(null);
        when(campaign.stopByUser(subscription)).thenReturn(true);
        when(persistence.stopByUser(subscription)).thenReturn(true);

        service.stopByUser(subscriberNumber, programType);

        verify(validation).validateSubscriptionToStop(subscriberNumber, programType);
        verify(campaign, never()).stopByUser(subscription);
        verify(persistence, never()).stopByUser(subscription);
    }

    @Test
    public void shouldFindSubscriptionByMobileNumberUsingRepository() {
        String subscriberNumber = "123";
        String program = PREGNANCY;
        Subscription subscription = new Subscription();
        when(allSubscriptions.findActiveSubscriptionFor(subscriberNumber, program)).thenReturn(subscription);

        Subscription returned = service.findActiveSubscriptionFor(subscriberNumber, program);

        assertEquals(subscription, returned);
        verify(allSubscriptions).findActiveSubscriptionFor(subscriberNumber, program);
    }

    @Test
    public void shouldGetAllActiveSubscriptions() {
        String subscriberNumber = "9844321234";
        service.activeSubscriptions(subscriberNumber);
        verify(allSubscriptions).getAllActiveSubscriptionsForSubscriber(subscriberNumber);
    }


    @Test
    public void shouldDoNothingIfSubscriptionIsNotCompletedDuringProcessAfterEvent() {
        Subscription subscription = mock(Subscription.class);
        when(subscription.isCompleted()).thenReturn(false);

        service.rollOverByEvent(subscription);
        verify(subscription, never()).canRollOff();
    }

    @Test
    public void shouldStopIfSubscriptionIsCompletedAndCannotRollOffDuringProcessAfterEvent() {
        Subscription subscription = mock(Subscription.class);
        ProgramType programType = mock(ProgramType.class);

        when(programType.getMaxWeek()).thenReturn(35);
        when(subscription.isCompleted()).thenReturn(true);
        when(subscription.canRollOff()).thenReturn(false);
        when(subscription.getProgramType()).thenReturn(programType);
        when(subscription.rollOverProgramType()).thenReturn(programType);
        when(campaign.stopExpired(subscription)).thenReturn(true);
        when(persistence.stopExpired(subscription)).thenReturn(true);

        service.rollOverByEvent(subscription);

        verify(campaign).stopExpired(subscription);
        verify(persistence).stopExpired(subscription);
    }

    @Test
    public void shouldRollOverDuringDuringProcessAfterEvent() {
        Subscription source = mock(Subscription.class);
        Subscriber subscriber = mock(Subscriber.class);
        ProgramType programType = mock(ProgramType.class);
        Week week = new Week(4);

        when(source.getSubscriber()).thenReturn(subscriber);
        when(source.getProgramType()).thenReturn(programType);
        when(source.rollOverProgramType()).thenReturn(programType);
        when(source.currentWeek()).thenReturn(week);
        when(source.currentDay()).thenReturn(DayOfWeek.Sunday);
        when(source.isCompleted()).thenReturn(true);
        when(source.canRollOff()).thenReturn(true);
        when(programType.getRollOverProgramType()).thenReturn(programType);

        when(validation.rollOver(any(Subscription.class), any(Subscription.class))).thenReturn(true);
        when(campaign.rollOver(any(Subscription.class), any(Subscription.class))).thenReturn(true);
        when(persistence.rollOver(any(Subscription.class), any(Subscription.class))).thenReturn(true);

        service.rollOverByEvent(source);

        SubscriptionMatcher matcher = new SubscriptionMatcher(subscriber, programType, ACTIVE);
        verify(validation).rollOver(eq(source), argThat(matcher));
        verify(campaign).rollOver(eq(source), argThat(matcher));
        verify(persistence).rollOver(eq(source), argThat(matcher));

    }

    @Test
    public void shouldRollOverToNewChildCareProgramForUserResponse() {
        String subscriberNumber = "1235467";
        service = spy(service);

        Subscription pregnancySubscriptionWaitingForRollOver = subscriptionB(subscriberNumber, pregnancyProgramType, WAITING_FOR_ROLLOVER_RESPONSE).build();
        Subscription newChildCareSubscriptionForRollOver = subscriptionB(subscriberNumber, childCareProgramType, ACTIVE).build();
        when(service.rollOverSubscriptionFrom(pregnancySubscriptionWaitingForRollOver)).thenReturn(newChildCareSubscriptionForRollOver);

        Subscription existingChildCareSubscription = subscriptionB(subscriberNumber, childCareProgramType, ACTIVE).build();

        when(allSubscriptions.findBy(subscriberNumber, PREGNANCY, WAITING_FOR_ROLLOVER_RESPONSE)).thenReturn(pregnancySubscriptionWaitingForRollOver);
        when(allSubscriptions.findActiveSubscriptionFor(subscriberNumber, CHILDCARE)).thenReturn(existingChildCareSubscription);

        when(validation.rollOverToNewChildCareProgram(pregnancySubscriptionWaitingForRollOver, newChildCareSubscriptionForRollOver, existingChildCareSubscription)).thenReturn(true);
        when(campaign.rollOverToNewChildCareProgram(pregnancySubscriptionWaitingForRollOver, newChildCareSubscriptionForRollOver, existingChildCareSubscription)).thenReturn(true);
        when(persistence.rollOverToNewChildCareProgram(pregnancySubscriptionWaitingForRollOver, newChildCareSubscriptionForRollOver, existingChildCareSubscription)).thenReturn(true);

        service.retainOrRollOver(subscriberNumber, false);

        verify(validation).rollOverToNewChildCareProgram(pregnancySubscriptionWaitingForRollOver, newChildCareSubscriptionForRollOver, existingChildCareSubscription);
        verify(persistence).rollOverToNewChildCareProgram(pregnancySubscriptionWaitingForRollOver, newChildCareSubscriptionForRollOver, existingChildCareSubscription);
        verify(campaign).rollOverToNewChildCareProgram(pregnancySubscriptionWaitingForRollOver, newChildCareSubscriptionForRollOver, existingChildCareSubscription);
    }

    @Test
    public void shouldRetainExistingChildCareProgramForContinueWithExistingChildCareResponse() {
        String subscriberNumber = "1235467";

        Subscription pregnancySubscriptionWaitingForRollOver = subscriptionB(subscriberNumber, pregnancyProgramType, WAITING_FOR_ROLLOVER_RESPONSE).build();
        Subscription childCareSubscription = subscriptionB(subscriberNumber, childCareProgramType, ACTIVE).build();

        when(allSubscriptions.findBy(subscriberNumber, PREGNANCY, WAITING_FOR_ROLLOVER_RESPONSE)).thenReturn(pregnancySubscriptionWaitingForRollOver);
        when(allSubscriptions.findActiveSubscriptionFor(subscriberNumber, CHILDCARE)).thenReturn(childCareSubscription);

        when(validation.retainExistingChildCare(pregnancySubscriptionWaitingForRollOver, childCareSubscription)).thenReturn(true);
        when(campaign.retainExistingChildCare(pregnancySubscriptionWaitingForRollOver, childCareSubscription)).thenReturn(true);
        when(persistence.retainExistingChildCare(pregnancySubscriptionWaitingForRollOver, childCareSubscription)).thenReturn(true);

        service.retainOrRollOver(subscriberNumber, true);

        verify(validation).retainExistingChildCare(pregnancySubscriptionWaitingForRollOver, childCareSubscription);
        verify(persistence).retainExistingChildCare(pregnancySubscriptionWaitingForRollOver, childCareSubscription);
        verify(campaign).retainExistingChildCare(pregnancySubscriptionWaitingForRollOver, childCareSubscription);
    }

    private SubscriptionBuilder subscriptionB(String subscriberNumber, ProgramType programType, SubscriptionStatus status) {
        return new SubscriptionBuilder().withRegistrationDate(DateUtil.now()).withStatus(status)
                .withSubscriber(new Subscriber(subscriberNumber)).withType(programType);
    }

    private class SubscriptionMatcher extends ArgumentMatcher<Subscription> {
        private Subscriber subscriber;
        private ProgramType programType;
        private SubscriptionStatus status;

        private SubscriptionMatcher(Subscriber subscriber, ProgramType programType, SubscriptionStatus status) {
            this.subscriber = subscriber;
            this.programType = programType;
            this.status = status;
        }

        @Override
        public boolean matches(Object o) {
            Subscription subscription = (Subscription) o;
            return subscriber.equals(subscription.getSubscriber())
                    && programType.equals(subscription.getProgramType())
                    && status.equals(subscription.getStatus());
        }
    }


}
