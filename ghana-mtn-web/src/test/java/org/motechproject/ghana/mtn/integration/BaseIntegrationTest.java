package org.motechproject.ghana.mtn.integration;

import org.hamcrest.Matchers;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.mtn.BaseSpringTestContext;
import org.motechproject.ghana.mtn.controller.SubscriptionController;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.parser.RegisterProgramMessageParser;
import org.motechproject.ghana.mtn.process.CampaignProcess;
import org.motechproject.ghana.mtn.repository.*;
import org.motechproject.ghana.mtn.tools.seed.MessageSeed;
import org.motechproject.ghana.mtn.tools.seed.ShortCodeSeed;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduler;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static junit.framework.Assert.*;
import static org.apache.commons.lang.StringUtils.replace;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.motechproject.ghana.mtn.domain.MessageBundle.ENROLLMENT_SUCCESS;
import static org.motechproject.ghana.mtn.domain.MessageBundle.PROGRAM_NAME_MARKER;
import static org.motechproject.ghana.mtn.process.CampaignProcess.DATE_MARKER;
import static org.motechproject.server.messagecampaign.EventKeys.BASE_SUBJECT;
import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;
import static org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduler.*;

public abstract class BaseIntegrationTest extends BaseSpringTestContext {

    public static final String REPEAT = "-repeat";
    @Autowired
    protected SubscriptionController subscriptionController;
    @Autowired
    protected AllSubscriptions allSubscriptions;
    @Autowired
    protected AllSubscribers allSubscribers;
    @Autowired
    protected AllProgramTypes allProgramTypes;
    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;   
    @Autowired
    protected AllShortCodes allShortCodes;
    @Autowired
    protected AllSMSAudits allSMSAudits;
    @Autowired
    protected AllMessageCampaigns allMessageCampaigns;
    @Autowired
    protected AllMessages allMessages;
    @Autowired
    ShortCodeSeed shortCodeSeed;
    @Autowired
    MessageSeed messageSeed;
    @Autowired
    RegisterProgramMessageParser registerProgramMessageParser;

    public final ProgramType childCarePregnancyType = new ProgramTypeBuilder().withMinWeek(1)
            .withMaxWeek(52).withProgramKey(ProgramType.CHILDCARE).withProgramName("Child Care").withShortCode("C").withShortCode("c").build();
    public final ProgramType pregnancyProgramType = new ProgramTypeBuilder().withMinWeek(5).withMaxWeek(35)
            .withProgramKey(ProgramType.PREGNANCY).withProgramName("Pregnancy").withShortCode("P").withShortCode("p").withRollOverProgramType(childCarePregnancyType).build();

    protected void addSeedData() {
        shortCodeSeed.run();
        messageSeed.run();
    }

    protected void cleanData() {
        super.after();
        remove( allShortCodes, allProgramTypes, allMessages, allSubscriptions, allSubscribers);
        removeAllQuartzJobs();
    }

    private void remove(MotechBaseRepository... reps) {
        for (MotechBaseRepository rep : reps) {
            List<MotechBaseDataObject> all = rep.getAll();
            for (MotechBaseDataObject object : all) rep.remove(object);
        }
    }

    Subscription subscription(Subscription pregnancySubscription) {
        return allSubscriptions.get(pregnancySubscription.getId());
    }


    Subscription subscription(String subscriberNumber, String programType) {
        return allSubscriptions.findActiveSubscriptionFor(subscriberNumber, programType);
    }

    protected SubscriptionRequest request(String inputMessage, String subscriberNumber) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setInputMessage(inputMessage);
        subscriptionRequest.setSubscriberNumber(subscriberNumber);
        return subscriptionRequest;
    }

    protected void message(String subscriberNumber, String inputMessage) {
        subscriptionController.handle(request(inputMessage, subscriberNumber));
    }

    protected Subscription enroll(String subscriberNumber, String inputMessage, String programKey) {

        Subscription subscription = subscription(subscriberNumber, programKey);
        assertNull(subscription);

        subscriptionController.handle(request(inputMessage, subscriberNumber));

        subscription = subscription(subscriberNumber, programKey);

        assertEnrollmentDetails(subscription, registerProgramMessageParser.parse(inputMessage, subscriberNumber));
        return subscription;
    }

    protected void assertEnrollmentDetails(Subscription subscription, RegisterProgramSMS registerSms) {
        assertNotNull(subscription);
        ProgramType programType = allProgramTypes.findByCampaignShortCode(registerSms.getDomain().getProgramType().getShortCodes().get(0));
        assertThat(programType, new ProgramTypeMatcher(subscription.getProgramType()));
        assertThat(subscription.getStatus(), is(SubscriptionStatus.ACTIVE));
        assertEquals(subscription.getStartWeekAndDay().getWeek().getNumber(), registerSms.getDomain().getStartWeekAndDay().getWeek().getNumber());
        assertTrue(subscription.getStartWeekAndDay().getWeek().getNumber() >= programType.getMinWeek());
        assertTrue(subscription.getStartWeekAndDay().getWeek().getNumber() <= programType.getMaxWeek());
        assertEquals(registerSms.getFromMobileNumber(), subscription.getSubscriber().getNumber());

        assertCampaignSchedule(subscription);
        List<SMSAudit> smsAudits = lastNSms(1);        
        assertSMS(replaceDateMarker(messageFor(ENROLLMENT_SUCCESS, programType), subscription.getCycleStartDate().toDate()), smsAudits.get(0));
    }

    private String messageFor(String message, ProgramType programType, Object... params) {
        return replace(messageFor(message, params), PROGRAM_NAME_MARKER, programType.getProgramName());
    }

    private String replaceDateMarker(String text, Date date) {
        return replace(text, DATE_MARKER, CampaignProcess.friendlyDateFormatter.format(date));
    }

    private String messageFor(String billingSuccess, Object... params) {
        return format(allMessages.findBy(billingSuccess).getContent(), params);
    }


    protected void assertIfCampaignScheduleIsStopped(Subscription subscription) {
        String subscriberNumber = subscription.subscriberNumber();
        ProgramType programType = subscription.getProgramType();
        CampaignRequest campaignRequest = subscription.createCampaignRequest();
        String prefix = String.format("%s%s.%s.%s", BASE_SUBJECT, campaignRequest.campaignName(), campaignRequest.externalId(), programType.getProgramKey());
        String jobId = format("%s-%s.%s", prefix, campaignRequest.externalId(), subscriberNumber);

        try {
            JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobId, "default");
            CronTrigger cronTrigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(jobId, "default");
            assertNull(jobDetail);
            assertNull(cronTrigger);
        } catch (SchedulerException e) {
            throw new AssertionError(e);
        }
    }

    protected void assertCampaignSchedule(Subscription subscription) {
        String subscriberNumber = subscription.subscriberNumber();
        CampaignRequest campaignRequest = subscription.createCampaignRequest();

        String messageKey = getMessageKey(subscription);
        String jobId = String.format("%s-%s%s.%s.%s-%s", INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, EventKeys.BASE_SUBJECT, campaignRequest.campaignName(), campaignRequest.externalId(), messageKey, "repeat");
        try {
            JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobId, "default");
            JobDataMap map = jobDetail.getJobDataMap();
            assertThat(map.get(EventKeys.EXTERNAL_ID_KEY).toString(), Matchers.is(subscriberNumber));
            assertThat(map.get(EventKeys.CAMPAIGN_NAME_KEY).toString(), Matchers.is(subscription.programKey()));
            assertThat(map.get("eventType").toString(), Matchers.is(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT));
        } catch (SchedulerException e) {
            throw new AssertionError(e);
        }
    }

    private String getMessageKey(Subscription subscription) {
        String messageKey = null;
        if (subscription.programName().equals("Pregnancy"))
            messageKey = "pregnancy-calendar-week-{Offset}-{WeekDay}";
        else
            messageKey = "childcare-calendar-week-{Offset}-{WeekDay}";
        return messageKey;
    }

    protected void assertSMS(String expected, SMSAudit smsAudit) {
        assertEquals(expected, smsAudit.getContent());
    }

    protected void assertSmsSent(String message) {
        List<SMSAudit> smsAudits = allSMSAudits.getAll();
        assertThat(smsAudits.get(smsAudits.size() - 1).getContent(), is(message));
    }

    protected List<SMSAudit> lastNSms(int count) {
        List<SMSAudit> smsAudits = allSMSAudits.getAll();
        return smsAudits.subList(smsAudits.size() - count, smsAudits.size());
    }
}
