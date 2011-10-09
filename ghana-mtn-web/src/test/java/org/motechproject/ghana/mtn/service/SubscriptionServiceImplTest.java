package org.motechproject.ghana.mtn.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.RegistrationBillingRequest;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.exception.UserRegistrationFailureException;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.testbuilders.TestSubscription;
import org.motechproject.ghana.mtn.testbuilders.TestSubscriptionRequest;
import org.motechproject.ghana.mtn.testbuilders.TestProgramType;
import org.motechproject.ghana.mtn.validation.InputMessageParser;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionServiceImplTest {
    private SubscriptionServiceImpl service;
    @Mock
    private AllSubscribers allSubscribers;
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private MessageCampaignService campaignService;
    @Mock
    private InputMessageParser inputMessageParser;
    @Mock
    private BillingService billingService;

    @Before
    public void setUp() {
        initMocks(this);
        service = new SubscriptionServiceImpl(allSubscribers, allSubscriptions, campaignService, inputMessageParser, billingService);
    }

    @Test
    public void shouldNotEnrollIfSubscriptionIsNotValid() {
        SubscriptionRequest subscriptionRequest = TestSubscriptionRequest.with("1234567890", "P 25");
        ProgramType programType = TestProgramType.with("Pregnancy", 3, 12, Arrays.asList("P"));
        Subscription subscription = TestSubscription.with(null, programType, DateTime.now(), new WeekAndDay(new Week(92), Day.MONDAY));

        when(inputMessageParser.parse("P 25")).thenReturn(subscription);

        String actualResponse = service.enroll(subscriptionRequest);
        assertEquals(MessageBundle.getMessage(MessageBundle.FAILURE_ENROLLMENT_MESSAGE), actualResponse);

        verify(allSubscriptions, never()).add(any(Subscription.class));
        verify(allSubscribers, never()).add(any(Subscriber.class));
        verify(campaignService, never()).startFor(any(CampaignRequest.class));
        verify(billingService, never()).processRegistration(Matchers.<RegistrationBillingRequest>any());
        assertEquals(null, subscription.getStatus());
    }


    @Test
    public void shouldNotEnrollIfSubscriberAlreadyHasAnActiveSubscriptionOfSameType() {
        SubscriptionRequest subscriptionRequest = TestSubscriptionRequest.with("1234567890", "P 25");
        ProgramType programType = TestProgramType.with("Pregnancy", 3, 12, Arrays.asList("P"));
        Subscription subscription = TestSubscription.with(null, programType, DateTime.now(), new WeekAndDay(new Week(12), Day.MONDAY));
        Subscription existingActiveSubscription = TestSubscription.with(null, programType, DateTime.now(), new WeekAndDay(new Week(31), Day.MONDAY));

        when(inputMessageParser.parse("P 25")).thenReturn(subscription);
        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber("1234567890")).thenReturn(Arrays.asList(existingActiveSubscription));

        String response = service.enroll(subscriptionRequest);
        assertEquals("You already have an active Pregnancy Program Subscription. Please wait for the program to complete, or stop it to start a new one", response);

        verify(allSubscriptions, never()).add(any(Subscription.class));
        verify(allSubscribers, never()).add(any(Subscriber.class));
        verify(campaignService, never()).startFor(any(CampaignRequest.class));
        verify(billingService, never()).processRegistration(Matchers.<RegistrationBillingRequest>any());
        assertEquals(null, subscription.getStatus());
    }

    @Test
    public void shouldPersistSubscriptionAndCampaignRequestForAValidSubscription() {
        SubscriptionRequest subscriptionRequest = TestSubscriptionRequest.with("1234567890", "P 25");
        ProgramType programType = TestProgramType.with("Pregnancy", 3, 12, Arrays.asList("P"));
        Subscription subscription = TestSubscription.with(null, programType, new DateTime(2011, 10, 8, 10, 10), new WeekAndDay(new Week(12), Day.MONDAY));

        when(inputMessageParser.parse("P 25")).thenReturn(subscription);
        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber("1234567890")).thenReturn(Collections.EMPTY_LIST);
        when(billingService.checkIfUserHasFunds(Matchers.<BillingServiceRequest>any())).thenReturn(new BillingServiceResponse());
        when(billingService.processRegistration(Matchers.<RegistrationBillingRequest>any())).thenReturn(new BillingServiceResponse());

        String response = service.enroll(subscriptionRequest);
        assertEquals("Welcome to Mobile Midwife Pregnancy Program. You are now enrolled & will receive SMSs full of great info every Mon,Weds &Fri.To stop these messages send STOP", response);

        verify(allSubscriptions).add(subscription);
        verify(allSubscribers).add(any(Subscriber.class));
        verify(campaignService).startFor(any(CampaignRequest.class));
        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertEquals(Day.MONDAY, subscription.getStartWeekAndDay().getDay());
    }

    @Test
    public void shouldNotPersistSubscriptionAndCreateCampaignRequest_IfBillingServiceRegistrationFails() {
        SubscriptionRequest subscriptionRequest = TestSubscriptionRequest.with("1234567890", "P 25");
        ProgramType programType = TestProgramType.with("Pregnancy", 3, 13, Arrays.asList("P"));
        Subscription subscription = TestSubscription.with(null, programType, new DateTime(2011, 10, 8, 10, 10), new WeekAndDay(new Week(12), Day.MONDAY));

        when(inputMessageParser.parse("P 25")).thenReturn(subscription);
        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber("1234567899")).thenReturn(Collections.EMPTY_LIST);
        when(billingService.checkIfUserHasFunds(Matchers.<BillingServiceRequest>any())).thenReturn(new BillingServiceResponse());
        BillingServiceResponse billingServiceResponse = new BillingServiceResponse();
        billingServiceResponse.addError(ValidationError.INSUFFICIENT_FUND);
        when(billingService.processRegistration(Matchers.<RegistrationBillingRequest>any())).thenReturn(billingServiceResponse);

        String response = service.enroll(subscriptionRequest);
        assertEquals("There aren’t sufficient funds to proceed with the registration.", response);

        verify(allSubscriptions, never()).add(subscription);
        verify(allSubscribers, never()).add(any(Subscriber.class));
        verify(campaignService, never()).startFor(any(CampaignRequest.class));
        assertEquals(null, subscription.getStatus());

    }

    @Test
    public void shouldReturnDefaultErrorMessage_IfUserRegistrationFailureMessageIsEmpty() {
        Subscription subscription = TestSubscription.with(null, TestProgramType.with("Pregnancy", 3, 13, Arrays.asList("P")),
                new DateTime(2011, 10, 8, 10, 10), new WeekAndDay(new Week(12), Day.MONDAY));

        when(inputMessageParser.parse("P 25")).thenReturn(subscription);
        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber("1234567899")).thenReturn(Collections.EMPTY_LIST);
        when(billingService.checkIfUserHasFunds(Matchers.<BillingServiceRequest>any())).thenThrow(new UserRegistrationFailureException(""));

        String response = service.enroll(TestSubscriptionRequest.with("1234567890", "P 25"));
        assertEquals("Sorry we are having trouble processing your request.", response);
    }
}
