package org.motechproject.ghana.mtn.process;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.dto.DefaultedBillingRequest;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.utils.DateUtils;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTimeUnit;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillingServiceMediatorTest {
    private BillingServiceMediator billingServiceMediator;
    private ProgramType programType = new ProgramTypeBuilder().withMaxWeek(35).withMinWeek(5).withShortCode("P").withProgramName("Pregnancy").build();
    @Mock
    private SMSService smsService;
    @Mock
    private BillingService billingService;
    @Mock
    private MessageBundle messageBundle;
    @Mock
    private AllSubscriptions allSubscriptions;

    @Before
    public void setUp() {
        initMocks(this);
        billingServiceMediator = new BillingServiceMediator(smsService, messageBundle, billingService, allSubscriptions);
    }

    @Test
    public void shouldUseBillingServiceToChargeFeeAndSendSMSAndStartDefaultedBillSchedule_InCaseOfErrors() {
        String mobileNumber = "123";
        String errorMsg = "error";
        Subscription subscription = subscription(mobileNumber, DateTime.now(), new Week(1), programType);
        CustomerBill customerBill = new CustomerBill("message", new Money(12d));
        BillingServiceResponse<CustomerBill> response = new BillingServiceResponse<CustomerBill>(customerBill);
        response.addError(ValidationError.INSUFFICIENT_FUNDS);

        when(billingService.chargeProgramFee(any(BillingServiceRequest.class))).thenReturn(response);
        when(messageBundle.get(Arrays.asList(ValidationError.INSUFFICIENT_FUNDS))).thenReturn(errorMsg);

        billingServiceMediator.chargeFeeAndHandleResponse(subscription);

        assertSmsRequest(mobileNumber, errorMsg);

        DateTime now = new DateUtils().startOfDay(DateUtil.now());
        ArgumentCaptor<DefaultedBillingRequest> defaultedBillingRequestCaptor = ArgumentCaptor.forClass(DefaultedBillingRequest.class);
        verify(billingService, times(2)).startDefaultedBillingSchedule(defaultedBillingRequestCaptor.capture());
        DefaultedBillingRequest dailyDefaultedBillingRequest = defaultedBillingRequestCaptor.getAllValues().get(0);
        assertThat(dailyDefaultedBillingRequest.getMobileNumber(), is(mobileNumber));
        assertThat(dailyDefaultedBillingRequest.getFrequency(), is(WallTimeUnit.Day));
        assertThat(dailyDefaultedBillingRequest.getCycleStartDate(), is(now.dayOfMonth().addToCopy(1)));
        assertThat(dailyDefaultedBillingRequest.getCycleEndDate(), is(now.dayOfMonth().addToCopy(7)));

        DefaultedBillingRequest weeklyDefaultedBillingRequest = defaultedBillingRequestCaptor.getAllValues().get(1);
        assertThat(weeklyDefaultedBillingRequest.getMobileNumber(), is(mobileNumber));
        assertThat(weeklyDefaultedBillingRequest.getFrequency(), is(WallTimeUnit.Week));
        assertThat(weeklyDefaultedBillingRequest.getCycleStartDate(), is(now.dayOfMonth().addToCopy(7 + 1)));
        assertThat(weeklyDefaultedBillingRequest.getCycleEndDate(), is(subscription.getCycleEndDate()));

        verify(allSubscriptions).update(subscription);
        assertThat(subscription.getStatus(), is(SubscriptionStatus.PAYMENT_DEFAULT));
    }

    @Test
    public void shouldChargeDefaultedSubscriptionAndUpdateSubscriptionToActiveIfBillingIsSuccessful() {

        String mobileNumber = "123";
        Subscription subscription = subscription(mobileNumber, DateTime.now(), new Week(1), programType);

        when(billingService.chargeProgramFee(Matchers.<BillingServiceRequest>any())).thenReturn(new BillingServiceResponse());
        billingServiceMediator.chargeFeeForDefaultedSubscription(subscription);

        verify(allSubscriptions).update(subscription);
        assertThat(subscription.getStatus(), is(SubscriptionStatus.ACTIVE));
    }

    private void assertSmsRequest(String mobileNumber, String errorMsg) {
        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();
        assertEquals(errorMsg, captured.getMessage());
        assertEquals(mobileNumber, captured.getMobileNumber());
    }

    private Subscription subscription(String mobileNumber, DateTime registeredDate, Week startWeek, ProgramType program) {
        Subscription subscription = new SubscriptionBuilder().withRegistrationDate(registeredDate).withStartWeekAndDay(new WeekAndDay(startWeek, Day.MONDAY))
                .withStatus(SubscriptionStatus.ACTIVE).withSubscriber(new Subscriber(mobileNumber))
                .withType(program).build();
        subscription.updateCycleInfo();
        return subscription;
    }

}
