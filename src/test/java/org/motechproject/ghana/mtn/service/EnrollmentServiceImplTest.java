package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.exception.MessageParsingFailedException;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.validation.InputMessageParser;
import org.motechproject.ghana.mtn.validation.InputMessageValidator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EnrollmentServiceImplTest {

    private EnrollmentServiceImpl enrollmentService;
    @Mock
    private InputMessageParser mockInputMessageParser;
    @Mock
    private InputMessageValidator mockInputMessageValidator;
    @Mock
    private AllSubscribers mockAllSubscribers;
    @Mock
    private AllSubscriptions mockAllSubscriptions;

    @Before
    public void setUp() {
        initMocks(this);
        enrollmentService = new EnrollmentServiceImpl(mockInputMessageValidator, mockInputMessageParser, mockAllSubscribers);
    }

    @Test
    public void ShouldParseAndValidateMessage() {
        String campaignType = "P";
        String startFrom = "10";
        String inputMessage = campaignType + " " + startFrom;
        Subscription subscription = new Subscription(campaignType, startFrom);

        when(mockInputMessageParser.parseMessage(inputMessage)).thenReturn(subscription);
        when(mockInputMessageValidator.validate(subscription)).thenReturn(true);

        String enrollmentResult = enrollmentService.parseAndValidateInputMessage(inputMessage);

        assertThat(enrollmentResult, is(String.format(EnrollmentService.SUCCESSFUL_ENROLLMENT_MESSAGE_FORMAT, SubscriptionType.PREGNANCY.getProgramName())));
        verify(mockInputMessageParser, times(1)).parseMessage(inputMessage);
        verify(mockInputMessageValidator, times(1)).validate(subscription);
    }

    @Test
    public void ShouldReturnFailureMessageWhenParsingFails() {
        String campaignType = "P";
        String startFrom = "10";
        String inputMessage = campaignType + " " + startFrom;

        when(mockInputMessageParser.parseMessage(anyString())).thenThrow(new MessageParsingFailedException(""));

        String enrollmentResult = enrollmentService.parseAndValidateInputMessage(inputMessage);

        assertThat(enrollmentResult, is(EnrollmentService.FAILURE_ENROLLMENT_MESSAGE));
        verify(mockInputMessageParser, times(1)).parseMessage(inputMessage);
        verify(mockInputMessageValidator, never()).validate(Matchers.<Subscription>any());
    }

    @Test
    public void ShouldReturnFailureMessageWhenValidationFails() {
        String campaignType = "P";
        String startFrom = "10";
        String inputMessage = campaignType + " " + startFrom;
        Subscription subscription = new Subscription(campaignType, startFrom);

        when(mockInputMessageParser.parseMessage(inputMessage)).thenReturn(subscription);
        when(mockInputMessageValidator.validate(subscription)).thenReturn(false);

        String enrollmentResult = enrollmentService.parseAndValidateInputMessage(inputMessage);

        assertThat(enrollmentResult, is(EnrollmentService.FAILURE_ENROLLMENT_MESSAGE));
        verify(mockInputMessageParser, times(1)).parseMessage(inputMessage);
        verify(mockInputMessageValidator, times(1)).validate(subscription);
    }

    @Test
    public void ShouldEnrollSubscriber() {
        String subscriberNumber = "1234567890";
        String campaignType = "P";
        String startFrom = "10";
        String inputMessage = campaignType + " " + startFrom;
        Subscription subscription = new Subscription(campaignType, startFrom);

        when(mockInputMessageParser.parseMessage(inputMessage)).thenReturn(subscription);
        when(mockInputMessageValidator.validate(subscription)).thenReturn(true);

        enrollmentService.enrollSubscriber(subscriberNumber, inputMessage);

        Subscriber subscriber = new Subscriber(subscriberNumber);
        verify(mockAllSubscribers).add(subscriber);
    }
}
