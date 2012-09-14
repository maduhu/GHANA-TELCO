package org.motechproject.ghana.telco.controller;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.*;
import org.motechproject.ghana.telco.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.telco.domain.vo.WeekAndDay;
import org.motechproject.ghana.telco.process.UserMessageParserProcess;
import org.motechproject.ghana.telco.repository.AllSubscriptions;
import org.motechproject.ghana.telco.service.SMSHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionControllerTest {
    @InjectMocks
    private SubscriptionController controller = new SubscriptionController();
    @Mock
    private UserMessageParserProcess parserHandle;
    @Mock
    private SMSHandler handler;
    @Mock
    AllSubscriptions allSubscriptions;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void ShouldParseAndValidateInputMessage() throws IOException {
        String subscriberNumber = "1234567890";
        String inputMessage = "C 25";

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setSubscriberNumber(subscriberNumber);
        subscriptionRequest.setInputMessage(inputMessage);

        Subscription subscription = mock(Subscription.class);
        SMS sms = spy(new RegisterProgramSMS(inputMessage, subscription).setFromMobileNumber(subscriberNumber));
        when(parserHandle.process(subscriberNumber, inputMessage)).thenReturn(sms);

        controller.handle(subscriptionRequest);
        
        verify(sms).process(handler);
    }

    @Test
    public void shouldFilterSubscriptionsBasedOnParam() {
        List<Subscription> subscriptions = Arrays.asList(new Subscription[]{
                new Subscription(new Subscriber("1"), new ProgramType().setProgramKey("Pregnancy"), SubscriptionStatus.ACTIVE, new WeekAndDay(), new DateTime()),
                new Subscription(new Subscriber("2"), new ProgramType().setProgramKey("Child care"), SubscriptionStatus.EXPIRED, new WeekAndDay(), new DateTime()),
                new Subscription(new Subscriber("3"), new ProgramType().setProgramKey("Pregnancy"), SubscriptionStatus.ACTIVE, new WeekAndDay(), new DateTime()),
                new Subscription(new Subscriber("4"), new ProgramType().setProgramKey("Pregnancy"), SubscriptionStatus.ROLLED_OFF, new WeekAndDay(), new DateTime()),
                new Subscription(new Subscriber("5"), new ProgramType().setProgramKey("Child care"), SubscriptionStatus.ACTIVE, new WeekAndDay(), new DateTime()),
                new Subscription(new Subscriber("6"), new ProgramType().setProgramKey("Pregnancy"), SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE, new WeekAndDay(), new DateTime())
        });
        doReturn(subscriptions).when(allSubscriptions).getAll();
        ModelAndView modelAndView = controller.search("", "/Pregnancy/Child care", "/ACTIVE");
        assertThat(((List<Subscription>)modelAndView.getModel().get("subscriptions")).size(), is(3));

    }



}
