package org.motechproject.ghana.telco.controller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.ghana.telco.domain.RegisterProgramSMS;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.parser.RegisterProgramMessageParser;
import org.motechproject.ghana.telco.repository.AllProgramTypes;
import org.motechproject.ghana.telco.repository.AllSubscriptions;
import org.motechproject.ghana.telco.service.SMSHandler;
import org.motechproject.ghana.telco.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.StringWriter;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@Controller
@RequestMapping("/api")
public class SubscriptionApiController {
    @Autowired
    private SMSHandler smsHandler;
    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    SubscriptionService subscriptionService;
    @Autowired
    RegisterProgramMessageParser registerProgramParser;
    @Autowired
    VelocityEngine velocityEngine;
    @Autowired
    private AllProgramTypes allProgramTypes;

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
        Template xmlTemplate = velocityEngine.getTemplate("/templates/responses/xmlResponse.vm");
        VelocityContext context = new VelocityContext();
        context.put("phoneNumber", subscriptionNo);
        if (registerProgramSMS == null) {
            context.put("status", "Failed");
            context.put("reason", "Start Time is not valid");
        } else {
            registerProgramSMS.process(smsHandler);
            context.put("status", "Success");
        }
        return getXmlString(xmlTemplate, context);
    }

    @RequestMapping("/search/{subscriberNumber}/{programCode}.json")
    @ResponseBody
    public String searchJson(@PathVariable String subscriberNumber, @PathVariable String programCode) throws JSONException {
        JSONObject responseObject = new JSONObject();
        List<Subscription> subscriptions = allSubscriptions.getAll();
        subscriptions = filter(having(on(Subscription.class).getSubscriber().getNumber(), containsString(subscriberNumber)), subscriptions);
        if (subscriptions.size() > 0)
            subscriptions = filter(having(on(Subscription.class).getProgramCode().toUpperCase(), is(programCode.toUpperCase())), subscriptions);
        responseObject.put("phoneNumber", subscriberNumber);
        if (subscriptions.size() > 0) {
            responseObject.put("status", "Success");
            responseObject.put("program", subscriptions.get(0).getProgramType().getProgramKey());
            responseObject.put("userStatus", subscriptions.get(0).getStatus().name());
        } else {
            responseObject.put("status", "Failed");
            responseObject.put("reason", "Not registered");
        }
        return responseObject.toString();
    }

    @RequestMapping("/search/{subscriberNumber}/{programCode}.xml")
    @ResponseBody
    public String searchXml(@PathVariable String subscriberNumber, @PathVariable String programCode) throws JSONException {
        Template xmlTemplate = velocityEngine.getTemplate("/templates/responses/xmlResponse.vm");
        VelocityContext context = new VelocityContext();
        List<Subscription> subscriptions = allSubscriptions.getAll();
        subscriptions = filter(having(on(Subscription.class).getSubscriber().getNumber(), containsString(subscriberNumber)), subscriptions);
        if (subscriptions.size() > 0)
            subscriptions = filter(having(on(Subscription.class).getProgramCode().toUpperCase(), is(programCode.toUpperCase())), subscriptions);
        context.put("phoneNumber", subscriberNumber);
        if (subscriptions.size() > 0) {
            context.put("status", "Success");
            context.put("program", subscriptions.get(0).getProgramType().getProgramKey());
            context.put("userStatus", subscriptions.get(0).getStatus().name());
        } else {
            context.put("status", "Failed");
            context.put("reason", "Not registered");
        }
        return getXmlString(xmlTemplate, context);
    }

    @RequestMapping("/unregister/{subscriberNumber}/{programCode}.json")
    @ResponseBody
    public String unRegisterJson(@PathVariable String subscriberNumber, @PathVariable String programCode) throws JSONException {
        JSONObject responseObject = new JSONObject();
        Subscription activeSubscription = allSubscriptions.findActiveSubscriptionFor(subscriberNumber, allProgramTypes.findByCampaignShortCode(programCode).getProgramKey());
        responseObject.put("phoneNumber", subscriberNumber);
        if (activeSubscription != null) {
            subscriptionService.stopByUser(subscriberNumber, activeSubscription.getProgramType());
            responseObject.put("status", "Success");
        } else {
            responseObject.put("status", "Failed");
            responseObject.put("reason", "Not registered");
        }
        return responseObject.toString();
    }

    @RequestMapping("/unregister/{subscriberNumber}/{programCode}.xml")
    @ResponseBody
    public String unRegisterXml(@PathVariable String subscriberNumber, @PathVariable String programCode) throws JSONException {
        Template xmlTemplate = velocityEngine.getTemplate("/templates/responses/xmlResponse.vm");
        VelocityContext context = new VelocityContext();
        Subscription activeSubscription = allSubscriptions.findActiveSubscriptionFor(subscriberNumber, allProgramTypes.findByCampaignShortCode(programCode).getProgramKey());
        context.put("phoneNumber", subscriberNumber);
        if (activeSubscription != null) {
            subscriptionService.stopByUser(subscriberNumber, activeSubscription.getProgramType());
            context.put("status", "Success");
        } else {
            context.put("status", "Failed");
            context.put("reason", "Not registered");
        }
        return getXmlString(xmlTemplate, context);
    }

    private String getXmlString(Template template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }
}