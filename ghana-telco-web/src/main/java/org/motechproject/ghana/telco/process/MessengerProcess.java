package org.motechproject.ghana.telco.process;

import org.motechproject.ghana.telco.domain.MessageBundle;
import org.motechproject.ghana.telco.domain.ProgramMessage;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.repository.AllProgramMessages;
import org.motechproject.ghana.telco.repository.AllSubscriptions;
import org.motechproject.ghana.telco.service.SMSService;
import org.motechproject.model.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessengerProcess extends BaseSubscriptionProcess {
    private AllProgramMessages allProgramMessages;
    private AllSubscriptions allSubscriptions;

    @Autowired
    protected MessengerProcess(SMSService smsService,
                               MessageBundle messageBundle,
                               AllProgramMessages allProgramMessages,
                               AllSubscriptions allSubscriptions) {

        super(smsService, messageBundle);
        this.allProgramMessages = allProgramMessages;
        this.allSubscriptions = allSubscriptions;
    }

    public void process(Subscription subscription, String messageKey, Time deliveryTime) {
        ProgramMessage message = allProgramMessages.findBy(messageKey);
        if (message == null) return;
        sendMessage(subscription, message.getContent(), deliveryTime);
    }
}
