package org.motechproject.ghana.mtn.controller;

import org.motechproject.ghana.mtn.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class EnrollmentController {

    public static final String RESPONSE_JSON_PREFIX = "{\"responseText\" : \"";
    private EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @RequestMapping("/enrollSubscriber")
    public void enrollSubscriber(@ModelAttribute("subscriberNumber") String subscriberNumber,
                                 @ModelAttribute("inputMessage") String inputMessage,
                                 HttpServletResponse httpServletResponse) throws IOException {
        String responseMessage = enrollmentService.enrollSubscriber(subscriberNumber, inputMessage);
        httpServletResponse.getWriter().write(RESPONSE_JSON_PREFIX + responseMessage + "\"}");
    }
}
