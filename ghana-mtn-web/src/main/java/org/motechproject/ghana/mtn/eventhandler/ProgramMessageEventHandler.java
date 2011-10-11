package org.motechproject.ghana.mtn.eventhandler;

import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.repository.AllProgramMessages;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

@Service
public class ProgramMessageEventHandler {
    private AllSubscriptions allSubscriptions;
    private AllProgramMessages allProgramMessages;
    private SMSService smsService;

    @Autowired
    public ProgramMessageEventHandler(AllSubscriptions allSubscriptions, AllProgramMessages allProgramMessages, SMSService smsService) {
        this.allSubscriptions = allSubscriptions;
        this.allProgramMessages = allProgramMessages;
        this.smsService = smsService;
    }

    @MotechListener(subjects = {MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
    public void sendMessageReminder(MotechEvent motechEvent) {
        Map params = motechEvent.getParameters();
        String programName = (String) params.get(EventKeys.CAMPAIGN_NAME_KEY);
        String subscriberNumber = (String) params.get(EventKeys.EXTERNAL_ID_KEY);

        Subscription subscription = allSubscriptions.findBy(subscriberNumber, programName);
        ProgramMessage message = allProgramMessages.findBy(subscription.getProgramType(), subscription.currentWeek(), subscription.currentDay());

        if (message == null) return;
        if (subscription.alreadySent(message)) return;
        sms(subscriberNumber, subscription, message);
        update(subscription);
    }

    private void update(Subscription subscription) {
        subscription.updateLastMessageSent();
        allSubscriptions.update(subscription);
    }

    private void sms(String subscriberNumber, Subscription subscription, ProgramMessage message) {
        SMSServiceRequest request = new SMSServiceRequest(subscriberNumber, message.getContent(), subscription.getProgramType());
        smsService.send(request);
    }


}
