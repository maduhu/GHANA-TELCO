package org.motechproject.ghana.telco.billing.integration;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.telco.billing.domain.BillAccount;
import org.motechproject.ghana.telco.billing.domain.BillAudit;
import org.motechproject.ghana.telco.billing.domain.BillStatus;
import org.motechproject.ghana.telco.billing.domain.TelcoMockUser;
import org.motechproject.ghana.telco.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.telco.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.telco.billing.dto.CustomerBill;
import org.motechproject.ghana.telco.billing.repository.AllBillAccounts;
import org.motechproject.ghana.telco.billing.repository.AllBillAudits;
import org.motechproject.ghana.telco.billing.repository.AllTelcoMockUsers;
import org.motechproject.ghana.telco.billing.service.BillingService;
import org.motechproject.ghana.telco.billing.service.BillingServiceImpl;
import org.motechproject.ghana.telco.domain.IProgramType;
import org.motechproject.ghana.telco.vo.Money;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.telco.billing.service.BillingScheduler.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContextBilling.xml"})
public class BillingServiceIntegrationTest {

    @Autowired
    private BillingService billingService;

    @Autowired
    private AllBillAccounts allBillAccounts;

    @Autowired
    private AllBillAudits allBillAudits;

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;
    @Autowired
    private AllTelcoMockUsers allTelcoMockUsers;
    private TelcoMockUser telcoMockUser = new TelcoMockUser("9876543210", new Money(10D));

    @Before
    public void setUp() {
        for (BillAudit billAudit : allBillAudits.getAll())
            allBillAudits.remove(billAudit);
        addNewSubscribers();
    }

    private void addNewSubscribers() {
        allTelcoMockUsers.add(telcoMockUser);
    }

    @Test
    public void ShouldStartABillingSchedule() throws SchedulerException {
        String subscriberNumber = "9876543210";
        DateTime startDate = new DateTime(2011, 10, 11, 0, 0);
        DateTime endDate = startDate.weekyear().addToCopy(35);
        BillingCycleRequest billingCycleRequest = new BillingCycleRequest(subscriberNumber, getPregnancyProgramType(), startDate, endDate);

        BillingServiceResponse<CustomerBill> billingServiceResponse = billingService.chargeAndStartBilling(billingCycleRequest);
        BillAccount billAccount = allBillAccounts.findByMobileNumber(subscriberNumber);
        List<BillAudit> billAudits = select(allBillAudits.getAll(), having(on(BillAudit.class).getMobileNumber(), equalTo(subscriberNumber)));

        String jobId = format("%s-%s.%s", MONTHLY_BILLING_SCHEDULE_SUBJECT, getPregnancyProgramType().getProgramKey(), subscriberNumber);

        JobDetail jobDetail = schedulerFactoryBean.getScheduler().getJobDetail(jobId, "default");

        CronTrigger cronTrigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(jobId, "default");

        assertThat(billingServiceResponse.getValue().getMessage(), is(BillingServiceImpl.BILLING_SCHEDULE_STARTED));
        assertThat(billingServiceResponse.getValue().getAmountCharged(), is(getPregnancyProgramType().getFee()));

        assertThat(billAccount.getProgramAccounts().size(), is(1));
        assertThat(billAccount.getProgramAccounts().get(0).getProgramKey(), is(getPregnancyProgramType().getProgramKey()));

        assertThat(billAudits.size(), is(1));
        assertThat(billAudits.get(0).getMobileNumber(), is(subscriberNumber));
        assertThat(billAudits.get(0).getAmountCharged(), equalTo(getPregnancyProgramType().getFee()));
        assertThat(billAudits.get(0).getBillStatus(), is(BillStatus.SUCCESS));
        assertThat(billAudits.get(0).getFailureReason(), is(""));

        JobDataMap map = jobDetail.getJobDataMap();
        assertThat(map.get(EXTERNAL_ID_KEY).toString(), is(subscriberNumber));
        assertThat(map.get(PROGRAM_KEY).toString(), is(getPregnancyProgramType().getProgramKey()));
        assertThat(map.get("eventType").toString(), is(MONTHLY_BILLING_SCHEDULE_SUBJECT));

        assertThat(cronTrigger.getCronExpression(), is("0 0 5 11 * ?"));
    }

    @After
    public void destroy() {
        allTelcoMockUsers.remove(allTelcoMockUsers.get(telcoMockUser.getId()));
        removeAllQuartzJobs();
    }

    private void removeAllQuartzJobs() {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            String[] groupNames = scheduler.getJobGroupNames();
            for (String group : groupNames) {
                String[] jobNames = scheduler.getJobNames(group);
                for (String job : jobNames)
                    scheduler.deleteJob(job, group);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public IProgramType getPregnancyProgramType() {
        return new IProgramType() {
            @Override
            public String getProgramName() {
                return "Pregnancy";
            }

            @Override
            public String getProgramKey() {
                return "PREGNANCY";
            }

            @Override
            public List<String> getShortCodes() {
                return Arrays.asList("P");
            }

            @Override
            public Integer getMinWeek() {
                return 5;
            }

            @Override
            public Integer getMaxWeek() {
                return 35;
            }

            @Override
            public Money getFee() {
                return new Money(0.60D);
            }
        };
    }

    public IProgramType getChildCareProgramType() {
        return new IProgramType() {
            @Override
            public String getProgramName() {
                return "Child Care";
            }

            @Override
            public String getProgramKey() {
                return "CHILDCARE";
            }

            @Override
            public List<String> getShortCodes() {
                return Arrays.asList("C");
            }

            @Override
            public Integer getMinWeek() {
                return 1;
            }

            @Override
            public Integer getMaxWeek() {
                return 52;
            }

            @Override
            public Money getFee() {
                return new Money(0.60D);
            }
        };
    }
}
