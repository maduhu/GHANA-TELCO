package org.motechproject.ghana.mtn.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionServiceRequest;
import org.motechproject.ghana.mtn.matchers.SubscriptionRequestMatcher;
import org.motechproject.ghana.mtn.service.SubscriptionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionControllerTest {
    private SubscriptionController controller;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;
    @Mock
    private PrintWriter writer;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new SubscriptionController(subscriptionService);
    }

    @Test
    public void ShouldParseAndValidateInputMessage() throws IOException {
        SubscriptionServiceRequest subscriptionRequest = new SubscriptionServiceRequest();
        subscriptionRequest.setSubscriberNumber("1234567890");
        subscriptionRequest.setInputMessage("C 25");

        controller.enroll(subscriptionRequest);

        verify(subscriptionService).startFor(argThat(new SubscriptionRequestMatcher("1234567890", "C 25")));
    }

}
