package org.motechproject.ghana.mtn.sms;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.sms.api.service.SmsService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class MTNServiceTest {

    MTNService service;
    @Mock
    SmsService smsService;

    @Before
    public void setUp() {
        initMocks(this);
        service = new MTNService(smsService);
    }


    @Test
    public void shouldSendSMSToTheGateWay() {
        String mobileNum = "987654321";
        String message = "Test Message";
        service.send(mobileNum, message);
        verify(smsService).sendSMS(mobileNum, message);
    }
}
