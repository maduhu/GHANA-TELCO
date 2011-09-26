package org.motechproject.ghana.mtn.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.service.EnrollmentService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EnrollmentControllerTest {
    EnrollmentController enrollmentController;
    @Mock
    private EnrollmentService mockEnrollmentService;
    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;

    @Before
    public void setUp() {
        initMocks(this);
        enrollmentController = new EnrollmentController(mockEnrollmentService);
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletResponse = new MockHttpServletResponse();
    }

    @Test
    public void ShouldParseAndValidateInputMessage() throws IOException {
        String subscriberNumber = "1234567890";
        String inputMessage = "C 25";
        String responseString = "{\"responseText\" : \"" + EnrollmentService.SUCCESSFUL_ENROLLMENT_MESSAGE + "\"}";
        when(mockEnrollmentService.enrollSubscriber(subscriberNumber, inputMessage)).thenReturn(EnrollmentService.SUCCESSFUL_ENROLLMENT_MESSAGE);
        enrollmentController.enrollSubscriber(subscriberNumber, inputMessage, mockHttpServletResponse);
        assertEquals(responseString, mockHttpServletResponse.getContentAsString());
    }
}
