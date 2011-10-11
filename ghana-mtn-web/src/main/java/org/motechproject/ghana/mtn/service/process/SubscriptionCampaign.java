package org.motechproject.ghana.mtn.service.process;

import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.service.sms.SMSService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionCampaign extends BaseSubscriptionProcess  implements ISubscriptionProcessFlow {
    private MessageCampaignService campaignService;

    @Autowired
    public SubscriptionCampaign(SMSService smsService, MessageBundle messageBundle, MessageCampaignService campaignService) {
        super(smsService, messageBundle);
        this.campaignService = campaignService;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        CampaignRequest campaignRequest = subscription.createCampaignRequest();
        campaignService.startFor(campaignRequest);
        return true;
    }

    @Override
    public Boolean stopFor(Subscription subscription) {
        CampaignRequest campaignRequest = subscription.createCampaignRequest();
        campaignService.stopFor(campaignRequest);
        return true;
    }
}
