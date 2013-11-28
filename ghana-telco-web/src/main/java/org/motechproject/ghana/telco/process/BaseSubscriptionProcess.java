package org.motechproject.ghana.telco.process;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.telco.domain.MessageBundle;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.sms.HTTPClient;
import org.motechproject.ghana.telco.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.telco.service.SMSService;

import static org.apache.commons.lang.StringUtils.replace;

public abstract class BaseSubscriptionProcess {

    private SMSService smsService;
    private MessageBundle messageBundle;
    private HTTPClient hc;

    protected BaseSubscriptionProcess(SMSService smsService, MessageBundle messageBundle) {
        this.smsService = smsService;
        this.messageBundle = messageBundle;
        this.hc = new HTTPClient();
    }

    protected String messageFor(String key) {
        return messageBundle.get(key);
    }

    protected void sendMessage(Subscription subscription, String content) {
        String message = replace(content, MessageBundle.PROGRAM_NAME_MARKER, subscription.programName());
        //SMSServiceRequest smsServiceRequest = new SMSServiceRequest(subscription.subscriberNumber(), message, subscription.getProgramType());
        // smsService.send(smsServiceRequest);
        this.hc.SendAtFee(subscription.subscriberNumber(),message);
    }

    protected void sendMessage(String mobileNumber, String content) {
        String message = replace(content, MessageBundle.PROGRAM_NAME_MARKER, StringUtils.EMPTY);
        //SMSServiceRequest smsServiceRequest = new SMSServiceRequest(mobileNumber, message);
        //smsService.send(smsServiceRequest);
        this.hc.SendAtFee(mobileNumber, message);
    }

    protected void sendMessageFree(Subscription subscription, String content) {
        String message = replace(content, MessageBundle.PROGRAM_NAME_MARKER, subscription.programName());
        this.hc.SendForFree(subscription.subscriberNumber(), message);
    }

    protected void sendMessageFree(String mobileNumber, String content)
    {
         this.hc.SendForFree(mobileNumber, content);
    }



}
