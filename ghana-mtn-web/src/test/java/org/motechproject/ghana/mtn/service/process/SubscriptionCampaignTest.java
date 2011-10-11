package org.motechproject.ghana.mtn.service.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;

import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class SubscriptionCampaignTest {
    private SubscriptionCampaign campaign;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;
    @Mock
    private MessageCampaignService campaignService;

    @Before
    public void setUp() {
        initMocks(this);
        campaign = new SubscriptionCampaign(smsService, messageBundle, campaignService);
    }

    @Test
    public void shouldAskSubscriptionForCampaignRequestAndSendToCampaignServiceForStart() {
        Subscription subscription = mock(Subscription.class);
        CampaignRequest campaignRequest = new CampaignRequest();
        when(subscription.createCampaignRequest()).thenReturn(campaignRequest);

        Boolean reply = campaign.startFor(subscription);
        assertTrue(reply);
        verify(campaignService).startFor(campaignRequest);
    }

    @Test
    public void shouldAskSubscriptionForCampaignRequestAndSendToCampaignServiceForStop() {
        Subscription subscription = mock(Subscription.class);
        CampaignRequest campaignRequest = new CampaignRequest();
        when(subscription.createCampaignRequest()).thenReturn(campaignRequest);

        Boolean reply = campaign.endFor(subscription);
        assertTrue(reply);
        verify(campaignService).stopFor(campaignRequest);
    }
}

