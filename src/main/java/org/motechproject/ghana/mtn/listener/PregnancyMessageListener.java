package org.motechproject.ghana.mtn.listener;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

@Service
public class PregnancyMessageListener {

    @Autowired
    SubscriptionService subscriptionService;

    private final static Logger log = Logger.getLogger(PregnancyMessageListener.class);

    @MotechListener(subjects = {MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
    public void handleWeeklyReminder(MotechEvent motechEvent) {
        Map<String,Object> params = motechEvent.getParameters();
        String programName = (String) params.get(EventKeys.CAMPAIGN_NAME_KEY);
        String subscriberNo = (String) params.get(EventKeys.EXTERNAL_ID_KEY);
        log.info("Program Name" + programName + ": Mobile Number" + subscriberNo);

        Subscription subscription = subscriptionService.findBy(subscriberNo, programName);
        sendReminder(subscription);
        
    }

    void sendReminder(Subscription subscription) {
        // Find the actual week for which the message has to be sent
        //  - find the Subscription - registered date and week- with the current date - what is the current week
        // Get the message based on the week from CMS Lite service
        if( subscription != null) {
            Week currentWeek = subscription.runningWeek();
            //cmsLiteService.getContent(new ResourceQuery())
        }
    }
}
