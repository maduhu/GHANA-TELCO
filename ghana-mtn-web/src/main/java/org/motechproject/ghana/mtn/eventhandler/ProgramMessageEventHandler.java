package org.motechproject.ghana.mtn.eventhandler;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.MessageAudit;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.repository.AllMessageAudits;
import org.motechproject.ghana.mtn.repository.AllProgramMessages;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

@Service
public class ProgramMessageEventHandler {
    private final static Logger log = Logger.getLogger(ProgramMessageEventHandler.class);
    private AllProgramMessages allSubscriptionMessages;
    private AllSubscriptions allSubscriptions;
    private AllMessageAudits allMessageAudits;

    @Autowired
    public ProgramMessageEventHandler(AllSubscriptions allSubscriptions, AllProgramMessages allSubscriptionMessages, AllMessageAudits allMessageAudits) {
        this.allSubscriptions = allSubscriptions;
        this.allSubscriptionMessages = allSubscriptionMessages;
        this.allMessageAudits = allMessageAudits;
    }

    @MotechListener(subjects = {MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
    public void sendMessageReminder(MotechEvent motechEvent) {
        Map params = motechEvent.getParameters();
        String programName = (String) params.get(EventKeys.CAMPAIGN_NAME_KEY);
        String subscriberNumber = (String) params.get(EventKeys.EXTERNAL_ID_KEY);

        Subscription subscription = allSubscriptions.findBy(subscriberNumber, programName);
        ProgramMessage message = allSubscriptionMessages.findBy(subscription.getProgramType(), subscription.currentWeek(), subscription.currentDay());

        if (message == null) return;
        if (subscription.alreadySent(message)) return;
        audit(programName, subscriberNumber, message);
        update(subscription);
    }

    private void audit(String programName, String subscriberNumber, ProgramMessage message) {
        log.info("Subscriber: " + subscriberNumber + ":" + message);
        MessageAudit audit = new MessageAudit(subscriberNumber, programName, DateUtil.now(), message.getContent());
        allMessageAudits.add(audit);
    }

    private void update(Subscription subscription) {
        subscription.updateLastMessageSent();
        allSubscriptions.update(subscription);
    }

}
