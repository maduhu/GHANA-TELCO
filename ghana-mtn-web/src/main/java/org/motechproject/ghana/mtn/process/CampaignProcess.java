package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.ghana.mtn.domain.MessageBundle.*;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

@Component
public class CampaignProcess extends BaseSubscriptionProcess implements ISubscriptionFlowProcess {
    private MessageCampaignService campaignService;
    private RollOverWaitSchedule rollOverWaitHandler;

    @Autowired
    public CampaignProcess(SMSService smsService, MessageBundle messageBundle, MessageCampaignService campaignService, RollOverWaitSchedule rollOverWaitHandler) {
        super(smsService, messageBundle);
        this.campaignService = campaignService;
        this.rollOverWaitHandler = rollOverWaitHandler;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        campaignService.startFor(subscription.createCampaignRequest());
        sendMessage(subscription, messageFor(ENROLLMENT_SUCCESS));
        return true;
    }

    @Override
    public Boolean stopExpired(Subscription subscription) {
        campaignService.stopFor(subscription.createCampaignRequest());
        sendMessage(subscription, messageFor(ENROLLMENT_STOPPED));
        return true;
    }

    @Override
    public Boolean stopByUser(Subscription subscription) {
        campaignService.stopFor(subscription.createCampaignRequest());
        sendMessage(subscription, messageFor(STOP_PROGRAM_SUCCESS));
        return true;
    }

    @Override
    public Boolean rollOver(Subscription fromSubscription, Subscription toSubscription) {
        if (WAITING_FOR_ROLLOVER_RESPONSE.equals(fromSubscription.getStatus())) {
            performScheduledWaitUntilUserResponds(fromSubscription);
            return true;
        }
        return performRollOver(fromSubscription, toSubscription, messageFor(ENROLLMENT_ROLlOVER));
    }

    private void performScheduledWaitUntilUserResponds(Subscription subscription) {
        rollOverWaitHandler.startScheduleWaitFor(subscription);
    }

    @Override
    public Boolean retainExistingChildCare(Subscription pregnancySubscriptionWaitingForRollOver, Subscription childCareSubscription) {
        campaignService.stopFor(pregnancySubscriptionWaitingForRollOver.createCampaignRequest());
        sendMessage(childCareSubscription.subscriberNumber(), messageFor(PENDING_ROLLOVER_RETAIN_CHILDCARE));
        return true;
    }

    @Override
    public Boolean rollOverToNewChildCareProgram(Subscription pregnancyProgramWaitingForRollOver, Subscription newChildCareToRollOver, Subscription existingChildCare) {
        campaignService.stopFor(existingChildCare.createCampaignRequest());
        performRollOver(pregnancyProgramWaitingForRollOver, newChildCareToRollOver, messageFor(PENDING_ROLLOVER_SWITCH_TO_NEW_CHILDCARE));
        return true;
    }

    private boolean performRollOver(Subscription fromSubscription, Subscription toSubscription, String message) {
        campaignService.stopFor(fromSubscription.createCampaignRequest());
        campaignService.startFor(toSubscription.createCampaignRequest());
        sendMessage(toSubscription, message);
        return true;
    }
}
