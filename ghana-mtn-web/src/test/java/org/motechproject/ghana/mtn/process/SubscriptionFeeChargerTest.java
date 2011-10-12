package org.motechproject.ghana.mtn.process;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.service.BillingService;
import org.motechproject.ghana.mtn.domain.MessageBundle;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.service.SMSService;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.ghana.mtn.vo.Money;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class SubscriptionFeeChargerTest {
    private SubscriptionFeeCharger charger;
    @Mock
    private SMSService smsService;
    @Mock
    private BillingService billingService;
    @Mock
    private MessageBundle messageBundle;

    @Before
    public void setUp() {
        initMocks(this);
        charger = new SubscriptionFeeCharger(smsService, messageBundle, billingService);
    }

    @Test
    public void shouldUseBillingServiceToChargeFeeAndSendSMSInCaseOfErrors() {
        String mobileNumber = "123";
        String errorMsg = "error";
        Subscription subscription = mock(Subscription.class);
        CustomerBill customerBill = new CustomerBill("message", new Money(12d));
        BillingServiceResponse<CustomerBill> response = new BillingServiceResponse<CustomerBill>(customerBill);
        response.addError(ValidationError.INSUFFICIENT_FUNDS);

        when(subscription.subscriberNumber()).thenReturn(mobileNumber);
        when(billingService.chargeProgramFee(any(BillingServiceRequest.class))).thenReturn(response);
        when(messageBundle.get(Arrays.asList(ValidationError.INSUFFICIENT_FUNDS))).thenReturn(errorMsg);

        charger.process(subscription);

        ArgumentCaptor<SMSServiceRequest> captor = ArgumentCaptor.forClass(SMSServiceRequest.class);
        verify(smsService).send(captor.capture());
        SMSServiceRequest captured = captor.getValue();
        assertEquals(errorMsg, captured.getMessage());
        assertEquals(mobileNumber, captured.getMobileNumber());
    }
}
