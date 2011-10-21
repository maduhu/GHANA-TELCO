package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.process.UserMessageParserProcess;
import org.motechproject.ghana.mtn.vo.Money;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSHandlerTest {

    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private UserMessageParserProcess parserHandle;
    private SMSHandler handler;

    public final ProgramType pregnancyProgramType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(5).withMaxWeek(35).withProgramName("Pregnancy").withShortCode("P").withShortCode("p").build();

    @Before
    public void setUp() {
        initMocks(this);
        handler = new SMSHandler(subscriptionService);
    }

    @Test
    public void ShouldRegisterSubscriberForProgram() throws IOException {
        String subscriberNumber = "1234567890";
        String inputMessage = "C 25";

        Subscription subscription = new SubscriptionBuilder().build();
        RegisterProgramSMS sms = (RegisterProgramSMS) new RegisterProgramSMS(inputMessage, subscription).setFromMobileNumber(subscriberNumber);

        handler.register(sms);

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionService).start(captor.capture());
        Subscriber subscriber = captor.getValue().getSubscriber();
        assertEquals(subscriberNumber, subscriber.getNumber());
    }

    @Test
    public void ShouldStopIfSubscriberSendsStop() {
        String stopMessage = "STOP";
        String subscriberNumber = "1234567890";

        StopSMS stopSMS = (StopSMS) new StopSMS(stopMessage,pregnancyProgramType).setFromMobileNumber(subscriberNumber);
        handler.stop(stopSMS);

        verify(subscriptionService).stopByUser(eq(subscriberNumber), refEq(pregnancyProgramType));
    }

    @Test
    public void ShouldCallRetainOrRollOverIfSubscriberSendsRetainOrRollOverSMS() {
        String subscriberNumber = "1234567890";

        RetainOrRollOverChildCareProgramSMS retainOrRollOverSMS = (RetainOrRollOverChildCareProgramSMS) new RetainOrRollOverChildCareProgramSMS(subscriberNumber, true)
                .setFromMobileNumber(subscriberNumber);
        handler.retainOrRollOverChildCare(retainOrRollOverSMS);

        verify(subscriptionService).retainOrRollOver(subscriberNumber, true);
    }
}
