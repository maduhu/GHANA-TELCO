package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.SMSAudit;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceResponse;
import org.motechproject.ghana.mtn.repository.AllSMSAudits;
import org.motechproject.ghana.mtn.sms.SMSProvider;
import org.motechproject.model.Time;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SMSServiceTest {

    @Autowired
    SMSService service;

    @Mock
    AllSMSAudits allProgramMessageAudits;

    @Mock
    SMSProvider smsProvider;

    @Before
    public void setUp() {
        initMocks(this);
        this.service = new SMSService(smsProvider,allProgramMessageAudits);
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

        ArgumentCaptor<SMSAudit> captor = ArgumentCaptor.forClass(SMSAudit.class);
        verify(smsProvider).send(mobileNumber, message, deliveryTime);
        verify(allProgramMessageAudits).add(captor.capture());
        SMSAudit capturedSMSAudit = captor.getValue();
        assertEquals(programType.getProgramKey(), capturedSMSAudit.getProgramKey());
        assertEquals(mobileNumber, capturedSMSAudit.getSubscriberNumber());
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

        ArgumentCaptor<SMSAudit> captor = ArgumentCaptor.forClass(SMSAudit.class);
        verify(smsProvider).send(mobileNumber, message, deliveryTime);
        verify(allProgramMessageAudits).add(captor.capture());
        SMSAudit capturedSMSAudit = captor.getValue();
        assertNull(capturedSMSAudit.getProgramKey());
        assertEquals(mobileNumber, capturedSMSAudit.getSubscriberNumber());
    }
}
