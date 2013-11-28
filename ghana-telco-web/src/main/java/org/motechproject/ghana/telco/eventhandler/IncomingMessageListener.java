package org.motechproject.ghana.telco.eventhandler;

import org.motechproject.ghana.telco.controller.SubscriptionController;
import org.motechproject.ghana.telco.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.telco.sms.HTTPClient;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.smpp.constants.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.motechproject.sms.smpp.constants.EventDataKeys.INBOUND_MESSAGE;
import static org.motechproject.sms.smpp.constants.EventDataKeys.SENDER;

@Service
public class IncomingMessageListener {

    private SubscriptionController controller;

    @Autowired
    public IncomingMessageListener(SubscriptionController controller) {
        this.controller = controller;
    }

    @MotechListener(subjects = {EventSubjects.INBOUND_SMS})
    public void processIncomingMessage(MotechEvent event) {
        Map<String,Object> incomingMessageParameters = event.getParameters();
        String messageSender = (String) incomingMessageParameters.get(SENDER);
        String message = (String) incomingMessageParameters.get(INBOUND_MESSAGE);

        String start_pattern = "^\\s*start\\s*";
        Pattern p = Pattern.compile(start_pattern,CASE_INSENSITIVE);
        if (p.matcher(message).find())
        {
            String response = "To register, send 'P' and weeks of pregnancy (5-35) or 'C' and age of child in months " +
                              "(1-12) e.g. P 24 or C 4. Send 'STOP' to unsubscribe.";

            HTTPClient cl = new HTTPClient();
            cl.SendForFree(messageSender, response);

        } else {
            controller.handle(subscriptionRequestFor(messageSender, message));
        }
    }

    private SubscriptionRequest subscriptionRequestFor(String messageSender, String message) {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setInputMessage(message);
        request.setSubscriberNumber(messageSender);
        return request;
    }
}
