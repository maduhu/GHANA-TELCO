package org.motechproject.ghana.telco.controller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.ghana.telco.domain.RegisterProgramSMS;
import org.motechproject.ghana.telco.parser.RegisterProgramMessageParser;
import org.motechproject.ghana.telco.process.UserMessageParserProcess;
import org.motechproject.ghana.telco.repository.AllSubscriptions;
import org.motechproject.ghana.telco.repository.AllUserActions;
import org.motechproject.ghana.telco.service.SMSHandler;
import org.motechproject.ghana.telco.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.StringWriter;

@Controller
@RequestMapping("/api")
public class SubscriptionApiController {
    public static final String UNREGISTER = "UNREGISTER";
    public static final String ROLL_OVER = "ROLL_OVER";
    @Autowired
    private UserMessageParserProcess subscriptionParser;
    @Autowired
    private SMSHandler smsHandler;
    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    SubscriptionService subscriptionService;
    @Autowired
    private AllUserActions allUserActions;
    @Autowired
    RegisterProgramMessageParser registerProgramParser;
    @Autowired
    VelocityEngine velocityEngine;

    @RequestMapping("/register/{subscriptionNo}/{program}/{startTime}.json")
    @ResponseBody
    public String registerJson(@PathVariable String subscriptionNo, @PathVariable String program, @PathVariable String startTime) throws JSONException {
        RegisterProgramSMS registerProgramSMS = registerProgramParser.parse(program + ' ' + startTime, subscriptionNo);
        JSONObject responseObject = new JSONObject();
        responseObject.put("phoneNumber", subscriptionNo);
        if (registerProgramSMS == null) {
            responseObject.put("status", "Failed");
            responseObject.put("reason", "Start Time is not valid");
        } else {
            registerProgramSMS.process(smsHandler);
            responseObject.put("status", "Success");
        }

        return responseObject.toString();
    }

    @RequestMapping("/register/{subscriptionNo}/{program}/{startTime}.xml")
    @ResponseBody
    public String registerXml(@PathVariable String subscriptionNo, @PathVariable String program, @PathVariable String startTime) throws JSONException {
        RegisterProgramSMS registerProgramSMS = registerProgramParser.parse(program + ' ' + startTime, subscriptionNo);
        Template xmlResponse = velocityEngine.getTemplate("/templates/responses/xmlResponse.vm");
        VelocityContext context = new VelocityContext();
        context.put("phoneNumber", subscriptionNo);
        if (registerProgramSMS == null) {
            context.put("status", "Failed");
            context.put("reason", "Start Time is not valid");
        } else {
            registerProgramSMS.process(smsHandler);
            context.put("status", "Success");
        }
        StringWriter writer = new StringWriter();
        xmlResponse.merge(context, writer);
        return writer.toString();
    }
}
