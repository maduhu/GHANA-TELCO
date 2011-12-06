package org.motechproject.ghana.mtn.process;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.AppConfig;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.repository.AllAppConfigs;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

import static org.motechproject.ghana.mtn.domain.MessageBundle.*;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

@Component
public class CampaignProcess extends BaseSubscriptionProcess implements ISubscriptionFlowProcess {
    private MessageCampaignService campaignService;
    private RollOverWaitSchedule rollOverWaitSchedule;
    public static final String DATE_MARKER = "${d}";
    public static SimpleDateFormat friendlyDateFormatter = new SimpleDateFormat("EEE, MMM d, ''yy");
    private AllAppConfigs allAppConfigs;

    @Autowired
    public CampaignProcess(SMSService smsService, MessageBundle messageBundle, MessageCampaignService campaignService, RollOverWaitSchedule rollOverWaitSchedule, AllAppConfigs allAppConfigs) {
        super(smsService, messageBundle);
        this.campaignService = campaignService;
        this.rollOverWaitSchedule = rollOverWaitSchedule;
        this.allAppConfigs = allAppConfigs;
    }

    @Override
    public Boolean startFor(Subscription subscription) {
        campaignService.startFor(subscription.createCampaignRegistrationRequest(getReminderTime(subscription.getCycleStartDate())));
        sendMessage(subscription, getSuccessMessage(subscription));
        return true;
    }

    private Time getReminderTime(DateTime cycleStartDate) {
        Time startTime = Time.parseTime(allAppConfigs.findByKey(AppConfig.WINDOW_START_TIME_KEY).value().toString(), ":");
        Time endTime = Time.parseTime(allAppConfigs.findByKey(AppConfig.WINDOW_END_TIME_KEY).value().toString(), ":");

        DateTime startDateTime = cycleStartDate.withTime(startTime.getHour(), startTime.getMinute(), 0, 0);
        DateTime endDateTime = cycleStartDate.withTime(endTime.getHour(), startTime.getMinute(), 0, 0);

        long reminderTimeInMillis = startDateTime.toDate().getTime() + cycleStartDate.toDate().getTime() % (endDateTime.toDate().getTime() - startDateTime.toDate().getTime());
        int reminderHour = DateTime.now().withMillis(reminderTimeInMillis).getHourOfDay();
        int reminderMinute = DateTime.now().withMillis(reminderTimeInMillis).getMinuteOfHour();

        return new Time(reminderHour, reminderMinute);
    }

    private String getSuccessMessage(Subscription subscription) {
        return StringUtils.replace(messageFor(ENROLLMENT_SUCCESS), DATE_MARKER, friendlyDateFormatter.format(subscription.getCycleStartDate().toDate()));
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
        return performRollOver(fromSubscription, toSubscription, messageFor(ENROLLMENT_ROLLOVER));
    }

    @Override
    public Boolean retainExistingChildCare(Subscription pregnancySubscriptionWaitingForRollOver, Subscription childCareSubscription) {
        unScheduleRollOverWait(pregnancySubscriptionWaitingForRollOver);
        campaignService.stopFor(pregnancySubscriptionWaitingForRollOver.createCampaignRequest());
        sendMessage(childCareSubscription.subscriberNumber(), messageFor(PENDING_ROLLOVER_RETAIN_CHILDCARE));
        return true;
    }

    @Override
    public Boolean rollOverToNewChildCareProgram(Subscription pregnancyProgramWaitingForRollOver, Subscription newChildCareToRollOver, Subscription existingChildCare) {
        unScheduleRollOverWait(pregnancyProgramWaitingForRollOver);
        campaignService.stopFor(existingChildCare.createCampaignRequest());
        performRollOver(pregnancyProgramWaitingForRollOver, newChildCareToRollOver, messageFor(PENDING_ROLLOVER_SWITCH_TO_NEW_CHILDCARE));
        return true;
    }

    private boolean performRollOver(Subscription fromSubscription, Subscription toSubscription, String message) {
        campaignService.stopFor(fromSubscription.createCampaignRequest());
        campaignService.startFor(toSubscription.createCampaignRegistrationRequest(getReminderTime(fromSubscription.getCycleStartDate())));
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
