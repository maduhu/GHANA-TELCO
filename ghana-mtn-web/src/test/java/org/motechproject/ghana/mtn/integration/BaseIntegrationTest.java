package org.motechproject.ghana.mtn.integration;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.mtn.BaseSpringTestContext;
import org.motechproject.ghana.mtn.billing.domain.*;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.billing.repository.AllBillAudits;
import org.motechproject.ghana.mtn.billing.repository.AllMTNMockUsers;
import org.motechproject.ghana.mtn.controller.SubscriptionController;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.repository.*;
import org.motechproject.ghana.mtn.tools.seed.MessageSeed;
import org.motechproject.ghana.mtn.tools.seed.ShortCodeSeed;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.CronBasedCampaignMessage;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static java.lang.String.format;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.*;
import static org.motechproject.server.messagecampaign.EventKeys.BASE_SUBJECT;
import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

public abstract class BaseIntegrationTest extends BaseSpringTestContext {

    @Autowired
    protected SubscriptionController subscriptionController;
    @Autowired
    protected AllSubscriptions allSubscriptions;
    @Autowired
    protected AllSubscribers allSubscribers;
    @Autowired
    protected AllProgramTypes allProgramTypes;
    @Autowired
    protected AllBillAccounts allBillAccounts;
    @Autowired
    private AllBillAudits allBillAudits;
    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;
    @Value("#{billingProperties['job.cron']}")
    String billingCron;
    @Autowired
    protected AllShortCodes allShortCodes;
    @Autowired
    AllMTNMockUsers allMtnMock;
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

    public final ProgramType childCarePregnancyType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(1)
            .withMaxWeek(52).withProgramKey(IProgramType.CHILDCARE).withProgramName("Child Care").withShortCode("C").withShortCode("c").build();
    public final ProgramType pregnancyProgramType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(5).withMaxWeek(35)
            .withProgramKey(IProgramType.PREGNANCY).withProgramName("Pregnancy").withShortCode("P").withShortCode("p").withRollOverProgramType(childCarePregnancyType).build();
    protected MTNMockUser mtnMockUser = new MTNMockUser("9500012345", new Money(1000D));

    protected void addSeedData() {
        shortCodeSeed.run();
        messageSeed.run();
    }

    protected void cleanData() {
        super.after();
        remove(allMtnMock, allShortCodes, allProgramTypes, allMessages, allSubscriptions, allSubscribers, allBillAudits, allBillAccounts);
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

        SubscriptionRequest subscriptionRequest = request(inputMessage, subscriberNumber);
        subscriptionController.handle(subscriptionRequest);

        subscription = subscription(subscriberNumber, programKey);
        assertEnrollmentDetails(subscription);
        return subscription;
    }

    protected void assertEnrollmentDetails(Subscription subscription) {
        assertNotNull(subscription);
        ProgramType programType = allProgramTypes.findByCampaignShortCode(subscription.getProgramType().getShortCodes().get(0));
        assertThat(programType, new ProgramTypeMatcher(subscription.getProgramType()));
        assertThat(subscription.getStatus(), is(SubscriptionStatus.ACTIVE));
        assertTrue(subscription.getStartWeekAndDay().getWeek().getNumber() >= programType.getMinWeek());
        assertTrue(subscription.getStartWeekAndDay().getWeek().getNumber() <= programType.getMaxWeek());
        assertNotNull(subscription.getSubscriber());

        assertCampaignSchedule(subscription);
        assertBillingScheduleAndAccount(subscription);
    }

    protected void assertBillingSchedule(Subscription subscription) {

        String subscriberNumber = subscription.subscriberNumber();
        ProgramType programType = subscription.getProgramType();
        String jobId = format("%s-%s.%s", MONTHLY_BILLING_SCHEDULE_SUBJECT, programType.getProgramKey(), subscriberNumber);

        try {
            JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobId, "default");
            JobDataMap map = jobDetail.getJobDataMap();
            assertThat(map.get(EXTERNAL_ID_KEY).toString(), Matchers.is(subscriberNumber));
            assertThat(map.get(PROGRAM_KEY).toString(), Matchers.is(subscription.programKey()));
            assertThat(map.get("eventType").toString(), Matchers.is(MONTHLY_BILLING_SCHEDULE_SUBJECT));

            CronTrigger cronTrigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(jobId, "default");
            assertThat(cronTrigger.getCronExpression(), Matchers.is(format(billingCron, subscription.getBillingStartDate().getDayOfMonth())));

        } catch (SchedulerException e) {
            throw new AssertionError(e);
        }
    }

    protected void assertBillingScheduleAndAccount(Subscription subscription) {
        assertBillingSchedule(subscription);
        assertBillAccount(subscription.subscriberNumber(), subscription.getProgramType());
        assertBillAudit(subscription.subscriberNumber(), subscription.getProgramType());
    }

    private void assertBillAudit(String subscriberNumber, ProgramType programType) {
        List<BillAudit> billAudits = select(allBillAudits.getAll(), having(on(BillAudit.class).getMobileNumber(), equalTo(subscriberNumber)));
        billAudits = select(billAudits, having(on(BillAudit.class).getProgram(), equalTo(programType.getProgramKey())));

        BillAudit billAudit = billAudits.get(billAudits.size() - 1);
        MatcherAssert.assertThat(billAudit.getMobileNumber(), Matchers.is(subscriberNumber));
        MatcherAssert.assertThat(billAudit.getAmountCharged(), equalTo(programType.getFee()));
        MatcherAssert.assertThat(billAudit.getBillStatus(), Matchers.is(BillStatus.SUCCESS));
        MatcherAssert.assertThat(billAudit.getFailureReason(), Matchers.is(""));
    }

    private void assertBillAccount(String subscriberNumber, ProgramType programType) {
        BillAccount billAccount = allBillAccounts.findByMobileNumber(subscriberNumber);
        BillProgramAccount billProgramAccount = selectFirst(billAccount.getProgramAccounts(), having(on(BillProgramAccount.class).getProgramKey(), equalTo(programType.getProgramKey())));
        MatcherAssert.assertThat(billProgramAccount.getProgramKey(), Matchers.is(programType.getProgramKey()));
        MatcherAssert.assertThat(billProgramAccount.getFee(), Matchers.is(programType.getFee()));
    }

    protected void assertIfBillingScheduleIsStopped(Subscription subscription) {
        String subscriberNumber = subscription.subscriberNumber();
        ProgramType programType = subscription.getProgramType();
        String jobId = format("%s-%s.%s", MONTHLY_BILLING_SCHEDULE_SUBJECT, programType.getProgramKey(), subscriberNumber);
        try {
            JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobId, "default");
            CronTrigger cronTrigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(jobId, "default");
            assertNull(jobDetail);
            assertNull(cronTrigger);
        } catch (SchedulerException e) {
            throw new AssertionError(e);
        }
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

        String cronMessageKey = "cron-message";
        String prefix = String.format("%s%s.%s.%s", BASE_SUBJECT, campaignRequest.campaignName(), campaignRequest.externalId(), cronMessageKey);
        String jobId = format("%s-%s", MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, prefix, subscriberNumber);
        try {
            JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobId, "default");
            CronTrigger cronTrigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(jobId, "default");

            JobDataMap map = jobDetail.getJobDataMap();
            assertThat(map.get(EXTERNAL_ID_KEY).toString(), Matchers.is(subscriberNumber));
            assertThat(map.get(EventKeys.CAMPAIGN_NAME_KEY).toString(), Matchers.is(subscription.programKey()));
            assertThat(map.get(EventKeys.SCHEDULE_JOB_ID_KEY).toString(), Matchers.is(prefix));
            assertThat(map.get("eventType").toString(), Matchers.is(MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT));

            CronBasedCampaignMessage campaignMessage = (CronBasedCampaignMessage) allMessageCampaigns.get(subscription.programKey(), cronMessageKey);
            assertThat(cronTrigger.getCronExpression(), Matchers.is(campaignMessage.cron()));

        } catch (SchedulerException e) {
            throw new AssertionError(e);
        }
    }

    protected void assertMessageSentToUser(String message) {
        List<SMSAudit> smsAudits = allSMSAudits.getAll();
        assertThat(smsAudits.get(smsAudits.size() - 1).getContent(), is(message));
    }
}
