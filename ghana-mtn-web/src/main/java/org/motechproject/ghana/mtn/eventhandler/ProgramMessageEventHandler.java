package org.motechproject.ghana.mtn.eventhandler;

import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.process.MessengerProcess;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

@Service
public class ProgramMessageEventHandler {
    private SubscriptionService service;
    private MessengerProcess messenger;

    @Autowired
    public ProgramMessageEventHandler(MessengerProcess messenger, SubscriptionService service) {
        this.messenger = messenger;
        this.service = service;
    }

    @MotechListener(subjects = {MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
    public void sendMessageReminder(MotechEvent event) {
        Map params = event.getParameters();
        String programName = (String) params.get(EventKeys.CAMPAIGN_NAME_KEY);
        String subscriberNumber = (String) params.get(EventKeys.EXTERNAL_ID_KEY);

        Subscription subscription = service.findBy(subscriberNumber, programName);
        messenger.process(subscription);
        service.processAfterEvent(subscription);
    }
}
