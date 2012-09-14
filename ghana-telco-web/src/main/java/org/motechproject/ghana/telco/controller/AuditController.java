package org.motechproject.ghana.telco.controller;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.motechproject.ghana.telco.repository.AllSubscriptions;
import org.motechproject.sms.api.SMSRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.is;

@Controller
public class AuditController {
    @Autowired
    private SmsAuditService smsAuditService;
    @Autowired
    private AllSubscriptions allSubscriptions;

    @RequestMapping("/audits/sms/outbound")
    public ModelAndView showAllOutboundSMSAudits() throws IOException {
        List<SMSRecord> allOutboundMessages = smsAuditService.allOutboundMessagesBetween(DateTime.now().minusDays(2), DateTime.now());
        List<SMSRecord> messageAudits = sort(allOutboundMessages, on(SMSRecord.class).getMessageTime(), sortComparator());

        return new ModelAndView("auditSms").addObject("time", DateUtil.now()).addObject("smsRecords", messageAudits);
    }

    @RequestMapping("/audits/sms/inbound")
    public ModelAndView showAllInboundSMSAudits() throws IOException {
        List<SMSRecord> allInboundMessages = smsAuditService.allInboundMessagesBetween(DateTime.now().minusDays(2), DateTime.now());
        List<SMSRecord> messageAudits = sort(allInboundMessages, on(SMSRecord.class).getMessageTime(), sortComparator());

        return new ModelAndView("auditSms").addObject("time", DateUtil.now()).addObject("smsRecords", messageAudits);
    }

    @RequestMapping("/filter/sms/outbound/for/{phoneNumber}")
    public ModelAndView showOutboundSMSAuditsForSubscriber(@PathVariable("phoneNumber") String phoneNumber) throws IOException {
        List<SMSRecord> allOutboundMessages = smsAuditService.allOutboundMessagesBetween(DateTime.now().minusDays(2), DateTime.now());
        List<SMSRecord> subscriberOutboundMessages = filter(having(on(SMSRecord.class).getPhoneNo(), is(phoneNumber)), allOutboundMessages);
        subscriberOutboundMessages = sort(subscriberOutboundMessages, on(SMSRecord.class).getMessageTime(), sortComparator());

        return new ModelAndView("auditSms").addObject("time", DateUtil.now()).addObject("smsRecords", subscriberOutboundMessages);
    }

    @RequestMapping("/filter/sms/inbound/for/{phoneNumber}")
    public ModelAndView showInboundSMSAuditsForSubscriber(@PathVariable("phoneNumber") String phoneNumber) throws IOException {
        List<SMSRecord> allInboundMessages = smsAuditService.allInboundMessagesBetween(DateTime.now().minusDays(2), DateTime.now());
        List<SMSRecord> subscriberInboundMessages = filter(having(on(SMSRecord.class).getPhoneNo(), is(phoneNumber)), allInboundMessages);
        subscriberInboundMessages = sort(subscriberInboundMessages, on(SMSRecord.class).getMessageTime(), sortComparator());

        return new ModelAndView("auditSms").addObject("time", DateUtil.now()).addObject("smsRecords", subscriberInboundMessages);
    }

    @RequestMapping("/filter/sms/all/for/{phoneNumber}")
    public ModelAndView showAllSMSAuditsForSubscriber(@PathVariable("phoneNumber") String phoneNumber) throws IOException {
        List<SMSRecord> allMessages = new ArrayList<SMSRecord>();
        List<SMSRecord> allInboundMessages = smsAuditService.allInboundMessagesBetween(DateTime.now().minusDays(2), DateTime.now());
        List<SMSRecord> allOutboundMessages = smsAuditService.allOutboundMessagesBetween(DateTime.now().minusDays(2), DateTime.now());
        CollectionUtils.addAll(allMessages, allOutboundMessages.iterator());
        CollectionUtils.addAll(allMessages, allInboundMessages.iterator());
        allMessages = filter(having(on(SMSRecord.class).getPhoneNo(), is(phoneNumber)), allMessages);
        allMessages = sort(allMessages, on(SMSRecord.class).getMessageTime(), sortComparator());

        return new ModelAndView("allAuditSms").addObject("smsRecords", allMessages);
    }

    private Comparator<DateTime> sortComparator() {
        return new Comparator<DateTime>() {
            @Override
            public int compare(DateTime o1, DateTime o2) {
                return o2.compareTo(o1);
            }
        };
    }
}
