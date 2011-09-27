package org.motechproject.ghana.mtn.service;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.exception.MessageParsingFailedException;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.validation.InputMessageParser;
import org.motechproject.ghana.mtn.validation.InputMessageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final static Logger log = Logger.getLogger(EnrollmentServiceImpl.class);
    private InputMessageValidator inputMessageValidator;
    private InputMessageParser inputMessageParser;
    private AllSubscribers allSubscribers;
    private AllSubscriptions allSubscriptions;

    @Autowired
    public EnrollmentServiceImpl(InputMessageValidator inputMessageValidator, InputMessageParser inputMessageParser, AllSubscribers allSubscribers) {
        this.inputMessageValidator = inputMessageValidator;
        this.inputMessageParser = inputMessageParser;
        this.allSubscribers = allSubscribers;
//        this.allSubscriptions = allSubscriptions;
    }

    @Override
    public String enrollSubscriber(String subscriberNumber, String inputMessage) {
        String enrollmentMessage = parseAndValidateInputMessage(inputMessage);
        if (!FAILURE_ENROLLMENT_MESSAGE.equals(enrollmentMessage)) {
            allSubscribers.add(new Subscriber(subscriberNumber));
        }
        return enrollmentMessage;
    }

    String parseAndValidateInputMessage(String inputMessage) {
        Subscription subscription;
        try {
            subscription = inputMessageParser.parseMessage(inputMessage);
        } catch (MessageParsingFailedException e) {
            log.error("Parsing failed.", e);
            return FAILURE_ENROLLMENT_MESSAGE;
        }

        if (isMessageValid(subscription))
            return String.format(SUCCESSFUL_ENROLLMENT_MESSAGE_FORMAT, subscription.getType().getProgramName());

        return FAILURE_ENROLLMENT_MESSAGE;
    }

    private boolean isMessageValid(Subscription inputMessage) {
        return inputMessageValidator.validate(inputMessage);
    }
}
