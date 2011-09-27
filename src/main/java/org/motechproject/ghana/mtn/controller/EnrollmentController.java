package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/subscription")
public class EnrollmentController {

    public static final String RESPONSE_JSON_PREFIX = "{\"responseText\" : \"";
    public static final String JSON_MIME_TYPE = "application/json";
    private EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @RequestMapping("/enroll")
    public void enrollSubscriber(@RequestParam("subscriberNumber") String subscriberNumber,
                                 @RequestParam("inputMessage") String inputMessage,
                                 HttpServletResponse httpServletResponse) throws IOException {
        String responseMessage = enrollmentService.enrollSubscriber(subscriberNumber, inputMessage);
        httpServletResponse.setContentType(JSON_MIME_TYPE);
        httpServletResponse.getWriter().write(RESPONSE_JSON_PREFIX + responseMessage + "\"}");
    }
}
