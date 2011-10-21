package org.motechproject.ghana.mtn.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.builder.ShortCodeBuilder;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.exception.InvalidProgramException;
import org.motechproject.ghana.mtn.matchers.SMSServiceRequestMatcher;
import org.motechproject.ghana.mtn.repository.AllShortCodes;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.util.DateUtil;

import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static junit.framework.Assert.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.mtn.TestData.childProgramType;
import static org.motechproject.ghana.mtn.TestData.pregnancyProgramType;
import static org.motechproject.ghana.mtn.domain.IProgramType.CHILDCARE;
import static org.motechproject.ghana.mtn.domain.IProgramType.PREGNANCY;
import static org.motechproject.ghana.mtn.domain.MessageBundle.*;
import static org.motechproject.ghana.mtn.domain.ShortCode.RETAIN_EXISTING_CHILDCARE_PROGRAM;
import static org.motechproject.ghana.mtn.domain.ShortCode.USE_ROLLOVER_TO_CHILDCARE_PROGRAM;

public class ValidationProcessorTest {

    private ValidationProcess validation;
    @Mock
    private SMSService smsService;
    @Mock
    private MessageBundle messageBundle;
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private BillingService billingService;
    @Mock
    private AllShortCodes allShortCodes;

    public final ProgramType childCareProgramType = pregnancyProgramType().build();
    public final ProgramType pregnancyProgramType = childProgramType().withRollOverProgramType(childCareProgramType).build();

    @Before
    public void setUp() {
        initMocks(this);
        validation = new ValidationProcess(smsService, messageBundle, allSubscriptions, billingService, allShortCodes);
    }

    @Test
    public void shouldReturnMessageForInvalidSubscription() {
        String mobileNumber = "123";
        String errMsg = "error msg";
        String programKey = CHILDCARE;

        Subscription subscription = mock(Subscription.class);
        ProgramType programType = mock(ProgramType.class);

        setupSubscriptionMock(mobileNumber, programKey, programType, subscription);
        when(subscription.isNotValid()).thenReturn(true);
        when(messageBundle.get(MessageBundle.REQUEST_FAILURE)).thenReturn(errMsg);

        Boolean reply = validation.startFor(subscription);

        assertFalse(reply);
        assertSMSRequest(mobileNumber, errMsg, programKey);
    }

    @Test
    public void shouldReturnErrorMessageIfActiveSubscriptionIsAlreadyPresent() {
        String mobileNumber = "123";
        String errMsg = "error msg %s";
        String programKey = PREGNANCY;
        String programName = "Pregnancy";

        Subscription subscription = mock(Subscription.class);
        ProgramType programType = mock(ProgramType.class);
        List<Subscription> dbSubscriptions = Arrays.asList(subscription);

        setupSubscriptionMock(mobileNumber, programKey, programType, subscription);
        when(subscription.isNotValid()).thenReturn(false);
        when(subscription.programKey()).thenReturn(programKey);
        when(subscription.programName()).thenReturn(programName);
        when(messageBundle.get(MessageBundle.ACTIVE_SUBSCRIPTION_PRESENT)).thenReturn(errMsg);
        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber(mobileNumber)).thenReturn(dbSubscriptions);

        Boolean reply = validation.startFor(subscription);

        assertFalse(reply);
        assertSMSRequest(mobileNumber, "error msg " + programName, programKey);
    }


    @Test
    public void shouldReturnMessageIfUserHasNoMoney() {
        String mobileNumber = "123";
        String errMsg = "error msg";
        String program = "program";

        Subscription subscription = mock(Subscription.class);
        ProgramType programType = mock(ProgramType.class);
        List<ValidationError> errors = new ArrayList<ValidationError>();
        BillingServiceResponse response = mock(BillingServiceResponse.class);


        setupSubscriptionMock(mobileNumber, program, programType, subscription);
        when(subscription.isNotValid()).thenReturn(false);
        when(messageBundle.get(errors)).thenReturn(errMsg);
        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber(mobileNumber)).thenReturn(Collections.EMPTY_LIST);
        when(response.hasErrors()).thenReturn(true);
        when(response.getValidationErrors()).thenReturn(errors);
        when(billingService.checkIfUserHasFunds(any(BillingServiceRequest.class))).thenReturn(response);

        Boolean reply = validation.startFor(subscription);

        assertFalse(reply);
        assertSMSRequest(mobileNumber, errMsg, program);
    }


    @Test
    public void shouldAskTheSourceSubscriptionIfItCanRollOver() {
        Subscription source = mock(Subscription.class);
        Subscription target = mock(Subscription.class);

        when(source.canRollOff()).thenReturn(true);

        Boolean reply = validation.rollOver(source, target);
        assertTrue(reply);
        verify(source).canRollOff();
    }

    @Test
    public void shouldCheckIfUserCanStopWithoutEnteringProgramType_OnlyIfEnrolledInOneSubscription() {
        String subscriberNumber = "9500012345";
        Subscription subscription = new SubscriptionBuilder().withSubscriber(new Subscriber(subscriberNumber)).withType(childCareProgramType).build();
        List<Subscription> subscriptions = asList(subscription);

        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber)).thenReturn(subscriptions);
        assertEquals(subscription, validation.validateSubscriptionToStop(subscriberNumber, null));
        assertEquals(subscription, validation.validateSubscriptionToStop(subscriberNumber, childCareProgramType));
    }

    @Test
    public void shouldCheckIfUserCanStopEnteringProgramType_IfEnrolledInTwoSubscriptions() {
        String subscriberNumber = "9500012345";
        Subscription subscription = subscriptionBuilder(subscriberNumber, pregnancyProgramType).build();
        Subscription subscription2 = subscriptionBuilder(subscriberNumber, childCareProgramType).build();
        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber)).thenReturn(asList(subscription, subscription2));

        Subscription actualSubscription2 = validation.validateSubscriptionToStop(subscriberNumber, childCareProgramType);
        assertEquals(subscription2, actualSubscription2);
    }

    @Test
    public void shouldSendErrorSMSIfUserEnrolledInTwoSubscriptionsTryToStopProgramWithoutEnteringProgramType() {
        String subscriberNumber = "9500012345";
        String errorMess = "error message";
        Subscription subscription = subscriptionBuilder(subscriberNumber, pregnancyProgramType).build();
        Subscription subscription2 = subscriptionBuilder(subscriberNumber, childCareProgramType).build();
        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber)).thenReturn(asList(subscription, subscription2));
        when(messageBundle.get(MessageBundle.STOP_SPECIFY_PROGRAM)).thenReturn(errorMess);

        Subscription actualSubscription = validation.validateSubscriptionToStop(subscriberNumber, null);
        assertNull(actualSubscription);
        assertSMSRequest(subscriberNumber, errorMess, null);
    }

    @Test
    public void shouldSendErrorSMSIfUserIsNotEnrolledInAnyProgramme() {
        String subscriberNumber = "9500012345";
        String errorMess = "not enrolled error message";
        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber)).thenReturn(Collections.<Subscription>emptyList());
        when(messageBundle.get(NOT_ENROLLED)).thenReturn(errorMess);

        Subscription actualSubscription = validation.validateSubscriptionToStop(subscriberNumber, childCareProgramType);
        assertNull(actualSubscription);
        assertSMSRequest(subscriberNumber, errorMess, null);
    }

    @Test
    public void shouldSendErrorSMSIfUserEnrolledTryToStopProgramWithWrongProgramType() {
        String subscriberNumber = "9500012345";
        String errorMess = "error message";
        Subscription subscription = subscriptionBuilder(subscriberNumber, pregnancyProgramType).build();
        when(allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber)).thenReturn(asList(subscription));
        when(messageBundle.get(NOT_ENROLLED)).thenReturn(errorMess);

        Subscription actualSubscription = validation.validateSubscriptionToStop(subscriberNumber, childCareProgramType);
        assertNull(actualSubscription);
        assertSMSRequest(subscriberNumber, errorMess, null);
    }

    @Test
    public void shouldValidateRollOverAndSendErrorMessageWhenUserHasNoSubscription() {
        String subscriberNumber = "9500012345";
        Date deliveryDate = DateUtil.newDate(2011, 10, 10).toDate();
        when(allSubscriptions.findActiveSubscriptionFor(subscriberNumber, PREGNANCY)).thenReturn(null);
        String errorMess = "error message";
        when(messageBundle.get(MessageBundle.ROLLOVER_INVALID_SUBSCRIPTION)).thenReturn(errorMess);

        Subscription actualSubscription = validation.validateForRollOver(subscriberNumber, deliveryDate);
        assertNull(actualSubscription);
        assertSMSRequest(subscriberNumber, errorMess, null);
    }

    @Test
    public void shouldValidateRollOverForChildCareAndSendDecisionMessageAndUpdateStatusWhenUserAlreadyHasExistingChildCare() {
        String subscriberNumber = "9500012345";
        ShortCode codeForRetainChildCare = new ShortCodeBuilder().withShortCode("E").build();
        ShortCode codeForRollOverChildCare = new ShortCodeBuilder().withShortCode("N").build();
        String decisionMessageToRollOver = format("You can have only one active child care program at a time. Please message \"%s\" to xxxx to retain the existing program and terminate the roll over. \\\n" +
                "  Please message \"%s\" to xxxx to continue with the roll over an terminate the existing program.", codeForRetainChildCare.defaultCode(), codeForRollOverChildCare.defaultCode());

        when(allShortCodes.getAllCodesFor(RETAIN_EXISTING_CHILDCARE_PROGRAM)).thenReturn(asList(codeForRetainChildCare));
        when(allShortCodes.getAllCodesFor(USE_ROLLOVER_TO_CHILDCARE_PROGRAM)).thenReturn(asList(codeForRollOverChildCare));
        when(messageBundle.get(ROLLOVER_NOT_POSSIBLE_PROGRAM_EXISTS_ALREADY)).thenReturn(decisionMessageToRollOver);

        Subscription existingChildcareSubscription = subscriptionBuilder(subscriberNumber, childCareProgramType).build();

        Subscription pregnancySubscription = subscriptionBuilder(subscriberNumber, pregnancyProgramType).build();
        Subscription childcareSubscription = subscriptionBuilder(subscriberNumber, childCareProgramType).build();
        when(allSubscriptions.findActiveSubscriptionFor(subscriberNumber, IProgramType.CHILDCARE)).thenReturn(existingChildcareSubscription);

        Boolean actualValidation = validation.rollOver(pregnancySubscription, childcareSubscription);
        assertTrue(actualValidation);
        assertSMSRequest(subscriberNumber, decisionMessageToRollOver, null);
        assertThat(pregnancySubscription.getStatus(), is(SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE));
        verify(allSubscriptions).update(pregnancySubscription);
    }

    @Test
    public void shouldValidateRollOverAndReturnSubscription() {
        String subscriberNumber = "9500012345";
        Date deliveryDate = DateUtil.newDate(2011, 10, 10).toDate();
        Subscription subscription = subscriptionBuilder(subscriberNumber, pregnancyProgramType).build();
        when(allSubscriptions.findActiveSubscriptionFor(subscriberNumber, PREGNANCY)).thenReturn(subscription);

        Subscription actualSubscription = validation.validateForRollOver(subscriberNumber, deliveryDate);
        assertNotNull(actualSubscription);
        verify(smsService, never()).send(Matchers.<SMSServiceRequest>any());
    }

    @Test
    public void shouldReturnTrueIfnPregnancySubscriptionWaitingForRollOver_OrChildCareSubscriptionExists() {

        String subscriberNumber = "9500012345";
        Subscription pregnancySubscription = subscriptionBuilder(subscriberNumber, pregnancyProgramType).build();

        assertTrue(validation.retainExistingChildCare(pregnancySubscription, mock(Subscription.class)));
        verifyZeroInteractions(smsService);
    }
    
    @Test
    public void shouldSendErrorMessageWhenPregnancySubscriptionWaitingForRollOver_OrChildCareSubscriptionDoesNotExist() {

        String subscriberNumber = "9500012345";
        Subscription pregnancySubscription = subscriptionBuilder(subscriberNumber, pregnancyProgramType).build();
        String errorMessage = "error message";
        when(messageBundle.get(ROLLOVER_NO_PENDING_PREGNANCY_PROGRAM)).thenReturn(errorMessage);

        assertFalse(validation.retainExistingChildCare(pregnancySubscription, null));
        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());

        assertThat(captor.getValue(), new SMSServiceRequestMatcher(subscriberNumber, errorMessage, null));
    }

    @Test
    public void shouldThrowExceptionWhenCustomerIsNotEnrolledForPregnancySubscription() {

        String errorMessage = "error message";
        when(messageBundle.get(NOT_ENROLLED)).thenReturn(errorMessage);

        try {
            validation.retainExistingChildCare(null, null);
            throw new AssertionError();
        } catch (InvalidProgramException e) {
            assertThat(e.getMessage(), is(errorMessage));
        }
        verifyZeroInteractions(smsService);
    }

    private void assertSMSRequest(String mobileNumber, String errorMsg, String programKey) {
        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();

        assertEquals(errorMsg, captured.getMessage());
        assertEquals(mobileNumber, captured.getMobileNumber());
        assertEquals(programKey, captured.programKey());
    }

    private void setupSubscriptionMock(String mobileNumber, String programKey, ProgramType programType, Subscription subscription) {
        when(programType.getProgramKey()).thenReturn(programKey);
        when(subscription.subscriberNumber()).thenReturn(mobileNumber);
        when(subscription.getProgramType()).thenReturn(programType);
    }

    private SubscriptionBuilder subscriptionBuilder(String subscriberNumber, ProgramType programType) {
        return new SubscriptionBuilder().withSubscriber(new Subscriber(subscriberNumber)).withType(programType);
    }
}
