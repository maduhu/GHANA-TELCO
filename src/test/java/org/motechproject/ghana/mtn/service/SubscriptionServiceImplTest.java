package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Before
    public void setUp() {
        initMocks(this);
        service = new SubscriptionServiceImpl(allSubscribers, allSubscriptions, campaignService, inputMessageParser);
    }

    @Test
    public void shouldPersistSubscriberAndSubscriptionIfItsAValidSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        String number = "1234567890";
        String inputMessage = "C 25";
        subscriptionRequest.setSubscriberNumber(number);
        subscriptionRequest.setInputMessage(inputMessage);
        Subscriber subscriber = new Subscriber(number);
        SubscriptionType subscriptionType = new SubscriptionTypeBuilder().withProgramName("Child Care").withShortCode("C").withMaxWeek(25).withMinWeek(10).build();

        Week week = new Week(10);
        Subscription subscription = new SubscriptionBuilder().withStatus(SubscriptionStatus.ACTIVE).withType(subscriptionType).withStartWeek(week).build();

        when(inputMessageParser.parse(inputMessage)).thenReturn(subscription);

        String enrollmentResponse = service.enroll(subscriptionRequest);

        verify(allSubscribers).add(argThat(new SubscriberMatcher(number)));
        verify(allSubscriptions).add(argThat(new SubscriptionMatcher(subscriber, subscriptionType, SubscriptionStatus.ACTIVE, week)));

        ArgumentCaptor<CampaignRequest> captor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(campaignService).startFor(captor.capture());
        CampaignRequest actualCampaignRequest = captor.getValue();
        CampaignRequest expectedCampaignRequest = new CampaignRequest();
        expectedCampaignRequest.setCampaignName(subscriptionType.getProgramName());
        expectedCampaignRequest.setExternalId(number);
        assertTrue(reflectionEquals(expectedCampaignRequest,actualCampaignRequest));

        assertEquals("Welcome to Mobile Midwife Child Care Program. " +
                "You are now enrolled & will receive SMSs full of great info every Mon,Weds &Fri." +
                "To stop these messages send STOP",
                enrollmentResponse);
    }



}
