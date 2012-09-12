package org.motechproject.ghana.telco.controller;

import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.SMS;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.telco.process.UserMessageParserProcess;
import org.motechproject.ghana.telco.repository.AllSubscriptions;
import org.motechproject.ghana.telco.service.SMSHandler;
import org.motechproject.ghana.telco.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
    private UserMessageParserProcess subscriptionParser;
    private SMSHandler smsHandler;
    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(UserMessageParserProcess subscriptionParser, SMSHandler smsHandler) {
        this.subscriptionParser = subscriptionParser;
        this.smsHandler = smsHandler;
    }

    @RequestMapping("/handle")
    @ResponseBody
    public void handle(@ModelAttribute SubscriptionRequest request) {
        SMS sms = subscriptionParser.process(request.getSubscriberNumber(), request.getInputMessage());
        if (sms == null) return;
        sms.process(smsHandler);
    }

    @RequestMapping("/search")
    public ModelAndView search() {
        ModelAndView modelAndView = new ModelAndView("searchResults");
        List<Subscription> allActiveSubscribers = getAllActiveSubscribers();
        modelAndView.addObject("subscriptions", allActiveSubscribers);
        return modelAndView;
    }

    @RequestMapping("/rollover/{subscriptionNumber}")
    @ResponseBody
    public String rollover(@PathVariable("subscriptionNumber") String subscriptionNumber) {
        subscriptionService.rollOver(subscriptionNumber);
        return "success";
    }

    @RequestMapping("/unregister/{subscriptionNumber}/{programType}")
    @ResponseBody
    public String unRegister(@PathVariable("subscriptionNumber") String subscriptionNumber, @PathVariable("programType") String programType) {
        subscriptionService.stopByUser(subscriptionNumber, new ProgramType().setProgramKey(programType));
        return "success";
    }

    private List<Subscription> getAllActiveSubscribers() {

        List<Subscription> subscriptionList = new ArrayList<Subscription>();
        for (Subscription subscription : allSubscriptions.getAllActiveSubscriptions(ProgramType.PREGNANCY)) {
            subscriptionList.add(subscription);
        }
        for (Subscription subscription : allSubscriptions.getAllActiveSubscriptions(ProgramType.CHILDCARE)) {
            subscriptionList.add(subscription);
        }
        return subscriptionList;
    }
}
