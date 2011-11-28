package org.motechproject.ghana.mtn.eventhandler;

import org.motechproject.ghana.mtn.process.RollOverWaitSchedule;
import org.motechproject.ghana.mtn.service.SubscriptionService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RollOverWaitScheduleEventHandler {
    private SubscriptionService subscriptionService;

    @Autowired
    public RollOverWaitScheduleEventHandler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @MotechListener(subjects = {RollOverWaitSchedule.ROLLOVER_WAIT_SCHEDULE})
    public void rollOverSchedule(MotechEvent event) {
        Map params = event.getParameters();
        String subscriberNumber = (String) params.get(EventKeys.EXTERNAL_ID_KEY);

        subscriptionService.retainOrRollOver(subscriberNumber, true);
    }
}
