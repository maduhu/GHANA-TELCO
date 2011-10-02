package org.motechproject.ghana.mtn.eventhandler;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionMessage;
import org.motechproject.ghana.mtn.repository.AllSubscriptionMessages;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

@Service
public class SubscriptionMessageEventHandler {
    private final static Logger log = Logger.getLogger(SubscriptionMessageEventHandler.class);
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private AllSubscriptionMessages allSubscriptionMessages;

    @MotechListener(subjects = {MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
    public void sendMessageReminder(MotechEvent motechEvent) {
        Map params = motechEvent.getParameters();
        String programName = (String) params.get(EventKeys.CAMPAIGN_NAME_KEY);
        String subscriberNo = (String) params.get(EventKeys.EXTERNAL_ID_KEY);

        Subscription subscription = subscriptionService.findBy(subscriberNo, programName);
        SubscriptionMessage subscriptionMessage = allSubscriptionMessages.findBy(subscription.getSubscriptionType(), subscription.currentWeek(), subscription.currentDay());
        if (subscription.canSend(subscriptionMessage))
            log.info("sent:" + subscriptionMessage);
        subscription.updateLastMessageSent();
    }

}
