package org.motechproject.ghana.telco.process;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.telco.domain.MessageBundle;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.service.SMSService;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

import static org.motechproject.ghana.telco.domain.MessageBundle.*;
import static org.motechproject.ghana.telco.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

@Component
public class CampaignProcess extends BaseSubscriptionProcess implements ISubscriptionFlowProcess {
    private MessageCampaignService campaignService;
    private RollOverWaitSchedule rollOverWaitSchedule;
    public static final String DATE_MARKER = "${d}";
    public static SimpleDateFormat friendlyDateFormatter = new SimpleDateFormat("EEE, MMM d, ''yy");

    @Autowired
    public CampaignProcess(SMSService smsService, MessageBundle messageBundle, MessageCampaignService campaignService, RollOverWaitSchedule rollOverWaitSchedule) {
        super(smsService, messageBundle);
        this.campaignService = campaignService;
        this.rollOverWaitSchedule = rollOverWaitSchedule;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        campaignService.startFor(subscription.createCampaignRegistrationRequest());
        sendMessage(subscription, getSuccessMessage(subscription));
        return true;
    }

    private String getSuccessMessage(Subscription subscription) {
        return StringUtils.replace(messageFor(ENROLLMENT_SUCCESS), DATE_MARKER, friendlyDateFormatter.format(subscription.getCycleStartDate().toDate()));
    }

    @Override
    public Boolean stopExpired(Subscription subscription) {
        campaignService.stopAll(subscription.createCampaignRequest());
        sendMessage(subscription, messageFor(ENROLLMENT_STOPPED));
        return true;
    }

    @Override
    public Boolean stopByUser(Subscription subscription) {
        campaignService.stopAll(subscription.createCampaignRequest());
        sendMessage(subscription, messageFor(STOP_PROGRAM_SUCCESS));
        return true;
    }

    @Override
    public Boolean rollOver(Subscription fromSubscription, Subscription toSubscription) {
        if (WAITING_FOR_ROLLOVER_RESPONSE.equals(fromSubscription.getStatus())) {
            performScheduledWaitUntilUserResponds(fromSubscription);
            return true;
        }
        return performRollOver(fromSubscription, toSubscription, messageFor(ENROLLMENT_ROLLOVER));
    }

    @Override
    public Boolean retainExistingChildCare(Subscription pregnancySubscriptionWaitingForRollOver, Subscription childCareSubscription) {
        unScheduleRollOverWait(pregnancySubscriptionWaitingForRollOver);
        campaignService.stopAll(pregnancySubscriptionWaitingForRollOver.createCampaignRequest());
        sendMessage(childCareSubscription.subscriberNumber(), messageFor(PENDING_ROLLOVER_RETAIN_CHILDCARE));
        return true;
    }

    @Override
    public Boolean rollOverToNewChildCareProgram(Subscription pregnancyProgramWaitingForRollOver, Subscription newChildCareToRollOver, Subscription existingChildCare) {
        unScheduleRollOverWait(pregnancyProgramWaitingForRollOver);
        campaignService.stopAll(existingChildCare.createCampaignRequest());
        performRollOver(pregnancyProgramWaitingForRollOver, newChildCareToRollOver, messageFor(PENDING_ROLLOVER_SWITCH_TO_NEW_CHILDCARE));
        return true;
    }

    private boolean performRollOver(Subscription fromSubscription, Subscription toSubscription, String message) {
        campaignService.stopAll(fromSubscription.createCampaignRequest());
        campaignService.startFor(toSubscription.createCampaignRegistrationRequest());
        sendMessage(toSubscription, message);
        return true;
    }

    private void performScheduledWaitUntilUserResponds(Subscription subscription) {
        rollOverWaitSchedule.startScheduleWaitFor(subscription);
    }

    private void unScheduleRollOverWait(Subscription subscription) {
        rollOverWaitSchedule.stopScheduleWaitFor(subscription);
    }
}
