package org.motechproject.ghana.mtn.service.process;

import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.validation.ValidationError;

import java.util.List;

public abstract class BaseSubscriptionProcess implements ISubscriptionProcess {
    private SMSService smsService;
    private MessageBundle messageBundle;

    protected BaseSubscriptionProcess(SMSService smsService, MessageBundle messageBundle) {
        this.smsService = smsService;
        this.messageBundle = messageBundle;
    }

    protected String messageFor(String key) {
        return messageBundle.get(key);
    }

    protected String messageFor(List<ValidationError> validationErrors) {
        return messageBundle.get(validationErrors);
    }

    protected void sendMessage(Subscription subscription, String content) {
        SMSServiceRequest smsServiceRequest = new SMSServiceRequest(subscription.subscriberNumber(), content, subscription.getProgramType());
        smsService.send(smsServiceRequest);
    }


}
