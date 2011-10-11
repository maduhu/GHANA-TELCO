package org.motechproject.ghana.mtn.process;

import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.repository.AllProgramMessages;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMessenger extends BaseSubscriptionProcess {
    private AllProgramMessages allProgramMessages;
    private AllSubscriptions allSubscriptions;

    @Autowired
    protected SubscriptionMessenger(SMSService smsService,
                                    MessageBundle messageBundle,
                                    AllProgramMessages allProgramMessages,
                                    AllSubscriptions allSubscriptions) {

        super(smsService, messageBundle);
        this.allProgramMessages = allProgramMessages;
        this.allSubscriptions = allSubscriptions;
    }

    public void process(Subscription subscription) {
        ProgramMessage message = allProgramMessages.findBy(subscription.getProgramType(), subscription.currentWeek(), subscription.currentDay());
        if (message == null) return;
        if (subscription.alreadySent(message)) return;

        subscription.updateLastMessageSent();
        allSubscriptions.update(subscription);
        sendMessage(subscription, message.getContent());
    }

}
