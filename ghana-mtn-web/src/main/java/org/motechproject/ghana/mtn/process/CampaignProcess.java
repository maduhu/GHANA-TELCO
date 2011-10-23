package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

@Component
public class CampaignProcess extends BaseSubscriptionProcess implements ISubscriptionFlowProcess {
    private MessageCampaignService campaignService;

    @Autowired
    public CampaignProcess(SMSService smsService, MessageBundle messageBundle, MessageCampaignService campaignService) {
        super(smsService, messageBundle);
        this.campaignService = campaignService;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        campaignService.startFor(subscription.createCampaignRequest());
        sendMessage(subscription, messageFor(MessageBundle.ENROLLMENT_SUCCESS));
        return true;
    }

    @Override
    public Boolean stopExpired(Subscription subscription) {
        campaignService.stopFor(subscription.createCampaignRequest());
        sendMessage(subscription, messageFor(MessageBundle.ENROLLMENT_STOPPED));
        return true;
    }

    @Override
    public Boolean stopByUser(Subscription subscription) {
        campaignService.stopFor(subscription.createCampaignRequest());
        sendMessage(subscription, messageFor(MessageBundle.STOP_PROGRAM_SUCCESS));
        return true;
    }

    @Override
    public Boolean rollOver(Subscription fromSubscription, Subscription toSubscription) {
        return WAITING_FOR_ROLLOVER_RESPONSE.equals(fromSubscription.getStatus()) || performRollOver(fromSubscription, toSubscription);
    }

    @Override
    public Boolean retainExistingChildCare(Subscription pregnancySubscriptionWaitingForRollOver, Subscription childCareSubscription) {
        campaignService.stopFor(pregnancySubscriptionWaitingForRollOver.createCampaignRequest());
        return true;
    }

    @Override
    public Boolean rollOverToNewChildCareProgram(Subscription pregnancyProgramWaitingForRollOver, Subscription newChildCareToRollOver, Subscription existingChildCare) {
        campaignService.stopFor(existingChildCare.createCampaignRequest());
        performRollOver(pregnancyProgramWaitingForRollOver, newChildCareToRollOver);
        return true;
    }

    private boolean performRollOver(Subscription fromSubscription, Subscription toSubscription) {
        campaignService.stopFor(fromSubscription.createCampaignRequest());
        campaignService.startFor(toSubscription.createCampaignRequest());
        sendMessage(toSubscription, messageFor(MessageBundle.ENROLLMENT_ROLlOVER));
        return true;
    }
}
