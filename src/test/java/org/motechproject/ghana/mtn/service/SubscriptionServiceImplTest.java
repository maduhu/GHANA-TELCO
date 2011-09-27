package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.matchers.SubscriberMatcher;
import org.motechproject.ghana.mtn.matchers.SubscriptionMatcher;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionServiceImplTest {
    private SubscriptionServiceImpl service;
    @Mock
    private AllSubscribers allSubscribers;
    @Mock
    private AllSubscriptions allSubscriptions;

    @Before
    public void setUp() {
        initMocks(this);
        service = new SubscriptionServiceImpl(allSubscribers, allSubscriptions);
    }

    @Test
    public void shouldPersistSubscriberAndSubscriptionIfItsAValidSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        String number = "1234567890";
        String inputMessage = "C 25";
        subscriptionRequest.setSubscriberNumber(number);
        subscriptionRequest.setInputMessage(inputMessage);
        Subscriber subscriber = new Subscriber(number);

        String enrollmentResponse = service.enroll(subscriptionRequest);

        verify(allSubscribers).add(argThat(new SubscriberMatcher(number)));
        verify(allSubscriptions).add(argThat(new SubscriptionMatcher(subscriber, SubscriptionType.CHILDCARE, SubscriptionStatus.ACTIVE, new Week(25))));

        assertEquals("Welcome to Mobile Midwife Child Care Program. " +
                "You are now enrolled & will receive SMSs full of great info every Mon,Weds &Fri." +
                "To stop these messages send STOP",
                enrollmentResponse);
    }
}
