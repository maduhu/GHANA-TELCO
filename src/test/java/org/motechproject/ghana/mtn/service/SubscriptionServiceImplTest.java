package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionTypeBuilder;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.matchers.SubscriberMatcher;
import org.motechproject.ghana.mtn.matchers.SubscriptionMatcher;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.validation.InputMessageParser;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;

import java.util.Arrays;
import java.util.Collections;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionServiceImplTest {
    private SubscriptionServiceImpl service;
    @Mock
    private AllSubscribers allSubscribers;
    @Mock
    private AllSubscriptions mockAllSubscriptions;
    @Mock
    private MessageCampaignService campaignService;
    @Mock
    private InputMessageParser inputMessageParser;

    @Before
    public void setUp() {
        initMocks(this);
        service = new SubscriptionServiceImpl(allSubscribers, mockAllSubscriptions, campaignService, inputMessageParser);
    }

    @Test
    public void shouldPersistSubscriberAndSubscriptionIfItsAValidSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        String number = "1234567890";
        String inputMessage = "C 25";
        subscriptionRequest.setSubscriberNumber(number);
        subscriptionRequest.setInputMessage(inputMessage);
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withProgramName("Child Care").withShortCode("C").withMaxWeek(25).withMinWeek(10).build();

        Week week = new Week(10);
        Subscription subscription = new SubscriptionBuilder().withStatus(SubscriptionStatus.ACTIVE).withType(subscriptionType).withStartWeek(week).build();

        when(inputMessageParser.parse(inputMessage)).thenReturn(subscription);

        SubscriptionServiceImpl spyService = spy(service);
        String enrollmentResponse = spyService.enroll(subscriptionRequest);

        InOrder inOrder = inOrder(spyService);
        inOrder.verify(spyService, times(1)).persistSubscription(subscription, number);
        inOrder.verify(spyService, times(1)).createCampaign(subscription);

        assertEquals("Welcome to Mobile Midwife Child Care Program. " +
                "You are now enrolled & will receive SMSs full of great info every Mon,Weds &Fri." +
                "To stop these messages send STOP",
                enrollmentResponse);
    }

    @Test
    public void shouldNotPersistSubscriptionIfSubscriberHasAValidSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        String number = "1234567890";
        String inputMessage = "C 25";
        subscriptionRequest.setSubscriberNumber(number);
        subscriptionRequest.setInputMessage(inputMessage);
        String programName = "Child Care";
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withProgramName(programName).withShortCode("C").withMaxWeek(25).withMinWeek(10).build();

        Week week = new Week(10);
        Subscription subscription = new SubscriptionBuilder().withStatus(SubscriptionStatus.ACTIVE).withType(subscriptionType).withStartWeek(week).build();

        when(inputMessageParser.parse(inputMessage)).thenReturn(subscription);

        SubscriptionServiceImpl spyService = spy(service);

        doReturn(true).when(spyService).hasActiveSubscriptionWithType(number, subscription.getSubscriptionType());
        String enrollmentResponse = spyService.enroll(subscriptionRequest);

        InOrder inOrder = inOrder(spyService);
        inOrder.verify(spyService, times(1)).hasActiveSubscriptionWithType(number, subscription.getSubscriptionType());
        inOrder.verify(spyService, never()).persistSubscription(subscription, number);
        inOrder.verify(spyService, never()).createCampaign(subscription);

        assertEquals("You already have an active " + programName + " Program Subscription. Please wait for the program to complete, or stop it to start a new one",
                enrollmentResponse);
    }

    @Test
    public void ShouldCreateCampaign() {
        String number = "1234567890";
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withShortCode("P")
                .withProgramName("Pregnancy").build();
        Subscriber subscriber = new Subscriber(number);
        Subscription subscription = new SubscriptionBuilder()
                .withType(subscriptionType).withSubscriber(subscriber).build();

        service.createCampaign(subscription);

        ArgumentCaptor<CampaignRequest> captor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(campaignService).startFor(captor.capture());
        CampaignRequest actualCampaignRequest = captor.getValue();
        CampaignRequest expectedCampaignRequest = new CampaignRequest();
        expectedCampaignRequest.setCampaignName(subscriptionType.getProgramName());
        expectedCampaignRequest.setExternalId(number);
        assertTrue(reflectionEquals(expectedCampaignRequest, actualCampaignRequest));
    }

    @Test
    public void ShouldCreateSubscription() {
        String number = "1234567890";
        Week week = new Week(10);
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withShortCode("P")
                .withProgramName("Pregnancy").build();
        Subscriber subscriber = new Subscriber(number);
        Subscription subscription = new SubscriptionBuilder().withSubscriber(subscriber).withStatus(SubscriptionStatus.ACTIVE).withType(subscriptionType).withStartWeek(week).build();

        service.persistSubscription(subscription, number);
        verify(allSubscribers).add(argThat(new SubscriberMatcher(subscriber.getNumber())));
        verify(mockAllSubscriptions).add(argThat(new SubscriptionMatcher(subscriber, subscriptionType, SubscriptionStatus.ACTIVE, week)));
    }

    @Test
    public void ShouldCheckForExistingSubscriptionAndReturnFalseIfNoActiveSubscriptionArePresent() {
        String number = "1234567890";
        Week week = new Week(10);
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withShortCode("P")
                .withMaxWeek(10).withMinWeek(1).withProgramName("Pregnancy").build();
        Subscriber subscriber = new Subscriber(number);
        Subscription subscription = new SubscriptionBuilder().withSubscriber(subscriber).withStatus(SubscriptionStatus.ACTIVE).withType(subscriptionType).withStartWeek(week).build();

        when(mockAllSubscriptions.getAllActiveSubscriptionsForSubscriber(number)).thenReturn(Collections.EMPTY_LIST);

        boolean hasSubscriptionOfType = service.hasActiveSubscriptionWithType(number, subscription.getSubscriptionType());

        assertFalse(hasSubscriptionOfType);
    }

    @Test
    public void ShouldCheckForExistingSubscriptionAndReturnTrueIfActiveSubscriptionIsPresent() {
        String number = "1234567890";
        Week week = new Week(10);
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withShortCode("P")
                .withMaxWeek(10).withMinWeek(1).withProgramName("Pregnancy").build();
        Subscriber subscriber = new Subscriber(number);
        Subscription subscription = new SubscriptionBuilder().withSubscriber(subscriber).withStatus(SubscriptionStatus.ACTIVE).withType(subscriptionType).withStartWeek(week).build();

        when(mockAllSubscriptions.getAllActiveSubscriptionsForSubscriber(number)).thenReturn(Arrays.asList(subscription));

        boolean hasSubscriptionOfType = service.hasActiveSubscriptionWithType(number, subscription.getSubscriptionType());

        assertTrue(hasSubscriptionOfType);
    }

    @Test
    public void ShouldCheckForExistingSubscriptionAndReturnTrueIfActiveSubscriptionOfSameTypeIsPresent() {
        String number = "1234567890";
        Week week = new Week(10);
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withShortCode("P")
                .withMaxWeek(10).withMinWeek(1).withProgramName("Pregnancy").build();
        Subscriber subscriber = new Subscriber(number);
        Subscription subscription = new SubscriptionBuilder().withSubscriber(subscriber).withStatus(SubscriptionStatus.ACTIVE).withType(subscriptionType).withStartWeek(week).build();

        when(mockAllSubscriptions.getAllActiveSubscriptionsForSubscriber(number)).thenReturn(Arrays.asList(subscription));

        boolean hasSubscriptionOfType = service.hasActiveSubscriptionWithType(number, subscription.getSubscriptionType());

        assertTrue(hasSubscriptionOfType);
    }

    @Test
    public void ShouldCheckForExistingSubscriptionAndReturnFalseIfActiveSubscriptionOfSameTypeIsNotPresent() {
        String number = "1234567890";
        Week week = new Week(10);
        SubscriptionType pregnancySubscriptionType = new SubscriptionTypeBuilder().withShortCode("P")
                .withMaxWeek(10).withMinWeek(1).withProgramName("Pregnancy").build();
        Subscriber subscriber = new Subscriber(number);

        Subscription pregnancySubscription = new SubscriptionBuilder().withSubscriber(subscriber).withStatus(SubscriptionStatus.ACTIVE).withType(pregnancySubscriptionType).withStartWeek(week).build();
        Subscription pregnancySubscriptionDuplicate = new SubscriptionBuilder().withSubscriber(subscriber).withStatus(SubscriptionStatus.ACTIVE).withType(pregnancySubscriptionType).withStartWeek(week).build();

        when(mockAllSubscriptions.getAllActiveSubscriptionsForSubscriber(number)).thenReturn(Arrays.asList(pregnancySubscription));

        boolean hasSubscriptionOfType = service.hasActiveSubscriptionWithType(subscriber.getNumber(), pregnancySubscriptionDuplicate.getSubscriptionType());

        assertTrue(hasSubscriptionOfType);
    }
}
