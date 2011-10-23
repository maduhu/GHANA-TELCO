package org.motechproject.ghana.mtn.process;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.TestData;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.ACTIVE;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

public class CampaignProcessorTest {
    private CampaignProcess campaign;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;
    @Mock
    private MessageCampaignService campaignService;

    public final ProgramType childCarePregnancyType = TestData.childProgramType().build();
    public final ProgramType pregnancyProgramType = TestData.pregnancyProgramType().build();
    
    @Before
    public void setUp() {
        initMocks(this);
        campaign = new CampaignProcess(smsService, messageBundle, campaignService);
    }

    @Test
    public void shouldAskSubscriptionForCampaignRequestAndSendToCampaignServiceForStart() {
        String message = "indie";
        Subscription subscription = mock(Subscription.class);
        CampaignRequest campaignRequest = new CampaignRequest();
        when(subscription.createCampaignRequest()).thenReturn(campaignRequest);
        when(messageBundle.get(MessageBundle.ENROLLMENT_SUCCESS)).thenReturn(message);

        Boolean reply = campaign.startFor(subscription);
        assertTrue(reply);
        verify(campaignService).startFor(campaignRequest);
        assertSMS(message);
    }

    @Test
    public void shouldAskSubscriptionForCampaignRequestAndSendToCampaignServiceForStop() {
        String message = "pop";
        Subscription subscription = mock(Subscription.class);
        CampaignRequest campaignRequest = new CampaignRequest();
        when(subscription.createCampaignRequest()).thenReturn(campaignRequest);
        when(messageBundle.get(MessageBundle.ENROLLMENT_STOPPED)).thenReturn(message);

        Boolean reply = campaign.stopExpired(subscription);
        assertTrue(reply);
        verify(campaignService).stopFor(campaignRequest);
        assertSMS(message);
    }

    @Test
    public void shouldAskSubscriptionForCampaignRequestAndSendToCampaignServiceForStopByUser() {
        String message = "pop";
        Subscription subscription = mock(Subscription.class);
        CampaignRequest campaignRequest = new CampaignRequest();
        when(subscription.createCampaignRequest()).thenReturn(campaignRequest);
        when(messageBundle.get(MessageBundle.STOP_PROGRAM_SUCCESS)).thenReturn(message);

        Boolean reply = campaign.stopByUser(subscription);
        assertTrue(reply);
        verify(campaignService).stopFor(campaignRequest);
        assertSMS(message);
    }

    @Test
    public void shouldRollOverUsingCampaignService() {
        String message = "rock & roll";
        Subscription source = mock(Subscription.class);
        Subscription target = mock(Subscription.class);
        CampaignRequest sourceRequest = mock(CampaignRequest.class);
        CampaignRequest targetRequest = mock(CampaignRequest.class);

        when(messageBundle.get(MessageBundle.ENROLLMENT_ROLlOVER)).thenReturn(message);
        when(source.createCampaignRequest()).thenReturn(sourceRequest);
        when(target.createCampaignRequest()).thenReturn(targetRequest);

        Boolean reply = campaign.rollOver(source, target);

        assertTrue(reply);
        verify(campaignService).stopFor(sourceRequest);
        verify(campaignService).startFor(targetRequest);
        assertSMS(message);
    }

    @Test
    public void shouldNotStartOrStopCampaign_WhenRollOverSubscriptionIsInWaitingForResponseStatusDuringRollOver() {
        Subscription source = mock(Subscription.class);
        Subscription target = mock(Subscription.class);

        when(source.getStatus()).thenReturn(WAITING_FOR_ROLLOVER_RESPONSE);

        Boolean reply = campaign.rollOver(source, target);

        assertTrue(reply);
        verifyZeroInteractions(campaignService, smsService);
    }
    
    @Test
    public void shouldStopCampaignForPregnancyWaitingForRollOverWhenUserWantsToRetainExistingChildCareSubscription() {
        Subscription source = mock(Subscription.class);
        Subscription target = mock(Subscription.class);
        CampaignRequest sourceRequest = mock(CampaignRequest.class);
        when(source.createCampaignRequest()).thenReturn(sourceRequest);        
        when(source.getStatus()).thenReturn(WAITING_FOR_ROLLOVER_RESPONSE);

        Boolean reply = campaign.retainExistingChildCare(source, target);

        assertTrue(reply);
        verify(campaignService).stopFor(sourceRequest);
        verifyNoMoreInteractions(campaignService);
        verifyZeroInteractions(campaignService, smsService);
    }

    @Test
    public void shouldStopCampaignForExistingChildCareProgramAndRollOverPregnancy_WhenUserWantsSelectsPregnancyRollOverWaitingProgram() {
        String subscriberNumber = "9500012345";
        Subscription pregnancySubscriptionToRollOver = subscriptionBuilder(subscriberNumber, pregnancyProgramType, WAITING_FOR_ROLLOVER_RESPONSE).build();
        Subscription newChildCareSubscriptionForRollOver = subscriptionBuilder(subscriberNumber, childCarePregnancyType, ACTIVE).build();
        Subscription existingChildCareSubscription = subscriptionBuilder(subscriberNumber, childCarePregnancyType, ACTIVE).build();
        when(messageBundle.get(MessageBundle.ENROLLMENT_ROLlOVER)).thenReturn("success");

        Boolean reply = campaign.rollOverToNewChildCareProgram(pregnancySubscriptionToRollOver, newChildCareSubscriptionForRollOver, existingChildCareSubscription);

        assertTrue(reply);
        ArgumentCaptor<CampaignRequest> captor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(campaignService, times(2)).stopFor(captor.capture());

        assertEquals(IProgramType.CHILDCARE, captor.getAllValues().get(0).campaignName());
        assertEquals(subscriberNumber, captor.getAllValues().get(0).externalId());
        assertEquals(IProgramType.PREGNANCY, captor.getAllValues().get(1).campaignName());
        assertEquals(subscriberNumber, captor.getAllValues().get(1).externalId());

        verifyStartCampaign(IProgramType.CHILDCARE, subscriberNumber);
        assertSMS("success");
    }

    private void verifyStartCampaign(String campaignName, String externalId) {
        ArgumentCaptor<CampaignRequest> captor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(campaignService).startFor(captor.capture());
        assertEquals(campaignName, captor.getValue().campaignName());
        assertEquals(externalId, captor.getValue().externalId());
    }

    private void assertSMS(String message) {
        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();
        assertEquals(message, captured.getMessage());
    }

    private SubscriptionBuilder subscriptionBuilder(String subscriberNumber, ProgramType programType, SubscriptionStatus status) {
        DateTime now = DateUtil.now();
        return new SubscriptionBuilder().withBillingStartDate(now).withRegistrationDate(now)
                .withSubscriber(new Subscriber(subscriberNumber))
                .withType(programType).withStatus(status);
    }
}

