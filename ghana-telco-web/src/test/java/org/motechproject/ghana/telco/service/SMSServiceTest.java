package org.motechproject.ghana.telco.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.telco.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.telco.domain.dto.SMSServiceResponse;
import org.motechproject.ghana.telco.sms.SMSProvider;
import org.motechproject.model.Time;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSServiceTest {

    @Autowired
    SMSService service;

    @Mock
    SMSProvider smsProvider;

    @Before
    public void setUp() {
        initMocks(this);
        this.service = new SMSService(smsProvider);
    }

    @Test
    public void ShouldSendSMSAndAudit() {
        String mobileNumber = "9876543210";
        ProgramType programType = new ProgramTypeBuilder().withProgramName("Pregnancy").build();
        String message = "Registration successful.";

        SMSServiceRequest smsServiceRequest = new SMSServiceRequest(mobileNumber, message, programType);
        Time deliveryTime = new Time(10, 30);
        smsServiceRequest.setDeliveryTime(deliveryTime);
        SMSServiceResponse smsServiceResponse = service.send(smsServiceRequest);

        assertTrue(smsServiceResponse.isSuccessful());

        verify(smsProvider).send(mobileNumber, message, deliveryTime);
    }

    @Test
    public void ShouldSendSMSAndAuditWithoutProgramType() {
        String mobileNumber = "9876543210";
        String message = "Registration successful.";

        Time deliveryTime = new Time(10, 30);
        SMSServiceRequest smsServiceRequest = new SMSServiceRequest(mobileNumber, message, null);
        smsServiceRequest.setDeliveryTime(deliveryTime);
        SMSServiceResponse smsServiceResponse = service.send(smsServiceRequest);

        assertTrue(smsServiceResponse.isSuccessful());

        verify(smsProvider).send(mobileNumber, message, deliveryTime);
    }
}
