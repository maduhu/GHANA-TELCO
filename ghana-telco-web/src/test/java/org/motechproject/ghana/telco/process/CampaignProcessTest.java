package org.motechproject.ghana.telco.process;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.telco.TestData;
import org.motechproject.ghana.telco.domain.*;
import org.motechproject.ghana.telco.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.telco.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.telco.domain.vo.Week;
import org.motechproject.ghana.telco.domain.vo.WeekAndDay;
import org.motechproject.ghana.telco.service.SMSService;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.telco.domain.MessageBundle.PENDING_ROLLOVER_SWITCH_TO_NEW_CHILDCARE;
import static org.motechproject.ghana.telco.domain.ProgramType.CHILDCARE;
import static org.motechproject.ghana.telco.domain.ProgramType.PREGNANCY;
import static org.motechproject.ghana.telco.domain.SubscriptionStatus.ACTIVE;
import static org.motechproject.ghana.telco.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

public class CampaignProcessTest {
    private CampaignProcess campaign;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;
    @Mock
    private MessageCampaignService campaignService;

    public final ProgramType childCarePregnancyType = TestData.childProgramType().build();
    public final ProgramType pregnancyProgramType = TestData.pregnancyProgramType().build();
    private String subscriberNumber = "9500044123";
    @Mock
    private RollOverWaitSchedule rollOverWaitHandler;

    @Before
    public void setUp() {
        initMocks(this);
        campaign = new CampaignProcess(smsService, messageBundle, campaignService, rollOverWaitHandler);
    }

    @Test
    public void shouldAskSubscriptionForCampaignRequestAndSendToCampaignServiceForStart() {
        String message = "indie";
        Subscription subscription = mockSubscription(subscriberNumber);
        CampaignRequest campaignRequest = new CampaignRequest();
        when(subscription.createCampaignRequest()).thenReturn(campaignRequest);
        when(subscription.getCycleStartDate()).thenReturn(DateUtil.now());
        when(messageBundle.get(MessageBundle.ENROLLMENT_SUCCESS)).thenReturn(message);

        Boolean reply = campaign.startFor(subscription);
        assertTrue(reply);
        verify(campaignService).startFor(campaignRequest);
        assertSMS(subscriberNumber, message);
    }

    @Test
    public void shouldAskSubscriptionForCampaignRequestAndSendToCampaignServiceForStop() {
        String message = "pop";
        Subscription subscription = mockSubscription(subscriberNumber);
        CampaignRequest campaignRequest = new CampaignRequest();
        when(subscription.createCampaignRequest()).thenReturn(campaignRequest);
        when(messageBundle.get(MessageBundle.ENROLLMENT_STOPPED)).thenReturn(message);

        Boolean reply = campaign.stopExpired(subscription);
        assertTrue(reply);
        verify(campaignService).stopAll(campaignRequest);
        assertSMS(subscriberNumber, message);
    }

    @Test
    public void shouldAskSubscriptionForCampaignRequestAndSendToCampaignServiceForStopByUser() {
        String message = "pop";
        Subscription subscription = mockSubscription(subscriberNumber);
        CampaignRequest campaignRequest = new CampaignRequest();
        when(subscription.createCampaignRequest()).thenReturn(campaignRequest);
        when(messageBundle.get(MessageBundle.STOP_PROGRAM_SUCCESS)).thenReturn(message);

        Boolean reply = campaign.stopByUser(subscription);
        assertTrue(reply);
        verify(campaignService).stopAll(campaignRequest);
        assertSMS(subscriberNumber, message);
    }

    @Test
    public void shouldRollOverUsingCampaignService() {
        String message = "rock & roll";
        Subscription source = mockSubscription(subscriberNumber);
        Subscription target = mockSubscription(subscriberNumber);
        CampaignRequest sourceRequest = mock(CampaignRequest.class);
        CampaignRequest targetRequest = mock(CampaignRequest.class);

        when(messageBundle.get(MessageBundle.ENROLLMENT_ROLLOVER)).thenReturn(message);
        when(source.createCampaignRequest()).thenReturn(sourceRequest);
        when(source.getCycleStartDate()).thenReturn(DateTime.now());
        when(target.createCampaignRequest()).thenReturn(targetRequest);

        Boolean reply = campaign.rollOver(source, target);

        assertTrue(reply);
        verify(campaignService).stopAll(sourceRequest);
        verify(campaignService).startFor(targetRequest);
        assertSMS(subscriberNumber, message);
    }

    @Test
    public void shouldNotStartOrStopCampaign_WhenRollOverSubscriptionIsInWaitingForResponseStatusDuringRollOverAndStartTheWaitForResponseSchedule() {
        Subscription source = mockSubscription(subscriberNumber);
        Subscription target = mockSubscription(subscriberNumber);

        when(source.getStatus()).thenReturn(WAITING_FOR_ROLLOVER_RESPONSE);

        Boolean reply = campaign.rollOver(source, target);

        assertTrue(reply);
        verify(rollOverWaitHandler).startScheduleWaitFor(source);
        verifyZeroInteractions(campaignService, smsService);
    }
    
    @Test
    public void shouldStopCampaignForPregnancyWaitingForRollOverWhenUserWantsToRetainExistingChildCareSubscriptionAndStopTheWaitResponseSchedule() {
        Subscription source = mockSubscription(subscriberNumber);
        Subscription target = mockSubscription(subscriberNumber);
        CampaignRequest sourceRequest = mock(CampaignRequest.class);
        when(source.createCampaignRequest()).thenReturn(sourceRequest);        
        when(source.getStatus()).thenReturn(WAITING_FOR_ROLLOVER_RESPONSE);
        String successMsg = "Your pregnancy care program was terminated based on your input. Your existing child care program will continue to be active. Thanks for using the Mobile Midwife service.";
        when(messageBundle.get(MessageBundle.PENDING_ROLLOVER_RETAIN_CHILDCARE)).thenReturn(successMsg);
        Boolean reply = campaign.retainExistingChildCare(source, target);

        assertTrue(reply);
        verify(rollOverWaitHandler).stopScheduleWaitFor(source);
        verify(campaignService).stopAll(sourceRequest);
        verifyNoMoreInteractions(campaignService);
        assertSMS(subscriberNumber, successMsg);
        verifyZeroInteractions(campaignService, smsService);
    }

    @Test
    public void shouldStopCampaignForExistingChildCareProgramAndRollOverPregnancyAndStopTheScheduledWaitForResponse() {
        String subscriberNumber = "9500012345";
        DateTime childCareCycleStartDate = DateUtil.now().monthOfYear().addToCopy(-5);
        DateTime pregnancyCycleStartDate = DateUtil.now().monthOfYear().addToCopy(-10);

        Subscription pregnancySubscriptionToRollOver = subscriptionBuilder(subscriberNumber, pregnancyProgramType, WAITING_FOR_ROLLOVER_RESPONSE).withCycleStartDate(pregnancyCycleStartDate).withStartWeekAndDay(new WeekAndDay(new Week(10), DayOfWeek.Monday)).build();
        Subscription newChildCareSubscriptionForRollOver = subscriptionBuilder(subscriberNumber, childCarePregnancyType, ACTIVE).withCycleStartDate(DateUtil.now()).withStartWeekAndDay(new WeekAndDay(new Week(10), DayOfWeek.Monday)).build();
        Subscription existingChildCareSubscription = subscriptionBuilder(subscriberNumber, childCarePregnancyType, ACTIVE).withCycleStartDate(childCareCycleStartDate).withStartWeekAndDay(new WeekAndDay(new Week(10), DayOfWeek.Monday)).build();

        String successMsg = "Your pregnancy care program was rolled over to child care program. Your existing child care program was terminated. Thanks for using the Mobile Midwife service.";
        when(messageBundle.get(PENDING_ROLLOVER_SWITCH_TO_NEW_CHILDCARE)).thenReturn(successMsg);

        Boolean reply = campaign.rollOverToNewChildCareProgram(pregnancySubscriptionToRollOver, newChildCareSubscriptionForRollOver, existingChildCareSubscription);

        assertTrue(reply);
        ArgumentCaptor<CampaignRequest> captor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(rollOverWaitHandler).stopScheduleWaitFor(pregnancySubscriptionToRollOver);
        verify(campaignService, times(2)).stopAll(captor.capture());

        assertEquals(CHILDCARE, captor.getAllValues().get(0).campaignName());
        assertEquals(subscriberNumber, captor.getAllValues().get(0).externalId());
        assertEquals(PREGNANCY, captor.getAllValues().get(1).campaignName());
        assertEquals(subscriberNumber, captor.getAllValues().get(1).externalId());

        verifyStartCampaign(CHILDCARE, subscriberNumber);
        assertSMS(subscriberNumber, successMsg);
        verifyNoMoreInteractions(smsService);
    }

    private void verifyStartCampaign(String campaignName, String externalId) {
        ArgumentCaptor<CampaignRequest> captor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(campaignService).startFor(captor.capture());
        assertEquals(campaignName, captor.getValue().campaignName());
        assertEquals(externalId, captor.getValue().externalId());
    }

    private void assertSMS(String subscriberNumber, String message) {
        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();
        assertEquals(message, captured.getMessage());
        assertEquals(subscriberNumber, captured.getMobileNumber());
    }

    private SubscriptionBuilder subscriptionBuilder(String subscriberNumber, ProgramType programType, SubscriptionStatus status) {
        DateTime now = DateUtil.now();
        return new SubscriptionBuilder().withRegistrationDate(now)
                .withSubscriber(new Subscriber(subscriberNumber))
                .withType(programType).withStatus(status);
    }

    private Subscription mockSubscription(String subscriberNumber) {
        Subscription subscription = mock(Subscription.class);
        when(subscription.subscriberNumber()).thenReturn(subscriberNumber);
        return subscription;
    }
}

