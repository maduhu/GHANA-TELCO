package org.motechproject.ghana.telco.eventhandler;

import org.motechproject.ghana.telco.controller.SubscriptionController;
import org.motechproject.ghana.telco.domain.dto.SubscriptionRequest;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.smpp.constants.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.motechproject.sms.api.constants.EventDataKeys.MESSAGE;
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
        String message = (String) incomingMessageParameters.get(MESSAGE);
        SubscriptionRequest request = new SubscriptionRequest();
        request.setInputMessage(message);
        request.setSubscriberNumber(messageSender);
        controller.handle(request);
    }
}
