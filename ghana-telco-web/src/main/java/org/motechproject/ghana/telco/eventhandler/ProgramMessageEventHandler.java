package org.motechproject.ghana.telco.eventhandler;

import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.process.MessengerProcess;
import org.motechproject.ghana.telco.service.SubscriptionService;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

@Service
public class ProgramMessageEventHandler {
    private SubscriptionService service;
    private MessengerProcess messenger;
    private AllMessageCampaigns allMessageCampaigns;

    @Autowired
    public ProgramMessageEventHandler(MessengerProcess messenger, SubscriptionService service, AllMessageCampaigns allMessageCampaigns) {
        this.messenger = messenger;
        this.service = service;
        this.allMessageCampaigns = allMessageCampaigns;
    }

    @MotechListener(subjects = {MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
    public void sendMessageReminder(MotechEvent event) {
        Map params = event.getParameters();
        String programKey = (String) params.get(EventKeys.CAMPAIGN_NAME_KEY);
        String subscriberNumber = (String) params.get(EventKeys.EXTERNAL_ID_KEY);

        Subscription subscription = service.findActiveSubscriptionFor(subscriberNumber, programKey);
        if (subscription != null) {
            messenger.process(subscription, (String) event.getParameters().get(EventKeys.MESSAGE_KEY), getCampaignDeliveryTime(subscription.programKey()));
            if (event.isLastEvent())
                service.rollOverByEvent(subscription);
        }
    }

    private Time getCampaignDeliveryTime(String programKey) {
        String messageName = "Pregnancy Message";
        if (programKey.equals(ProgramType.CHILDCARE)) {
            messageName = "ChildCare Message";
        }
        RepeatingCampaignMessage repeatingCampaignMessage = (RepeatingCampaignMessage) allMessageCampaigns.getCampaignMessageByMessageName(programKey, messageName);
        return repeatingCampaignMessage.deliverTime();
    }
}
