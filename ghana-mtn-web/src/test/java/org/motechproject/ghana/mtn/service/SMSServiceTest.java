package org.motechproject.ghana.mtn.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.SMSAudit;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceRequest;
import org.motechproject.ghana.mtn.domain.dto.SMSServiceResponse;
import org.motechproject.ghana.mtn.repository.AllSMSAudits;
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
        IProgramType programType = new ProgramTypeBuilder().withProgramName("Pregnancy").build();
        String message = "Registration successful.";

        SMSServiceResponse smsServiceResponse = service.send(new SMSServiceRequest(mobileNumber, message, programType));

        assertTrue(smsServiceResponse.isSuccessful());

        ArgumentCaptor<SMSAudit> captor = ArgumentCaptor.forClass(SMSAudit.class);
        verify(smsProvider).send(mobileNumber, message);
        verify(allProgramMessageAudits).add(captor.capture());
        SMSAudit capturedSMSAudit = captor.getValue();
        assertEquals(programType.getProgramName(), capturedSMSAudit.getProgramName());
        assertEquals(mobileNumber, capturedSMSAudit.getSubscriberNumber());
    }

    @Test
    public void ShouldSendSMSAndAuditWithoutProgramType() {
        String mobileNumber = "9876543210";
        String message = "Registration successful.";

        SMSServiceResponse smsServiceResponse = service.send(new SMSServiceRequest(mobileNumber, message, null));

        assertTrue(smsServiceResponse.isSuccessful());

        ArgumentCaptor<SMSAudit> captor = ArgumentCaptor.forClass(SMSAudit.class);
        verify(smsProvider).send(mobileNumber, message);
        verify(allProgramMessageAudits).add(captor.capture());
        SMSAudit capturedSMSAudit = captor.getValue();
        assertNull(capturedSMSAudit.getProgramName());
        assertEquals(mobileNumber, capturedSMSAudit.getSubscriberNumber());
    }
}
