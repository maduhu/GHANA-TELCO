package org.motechproject.ghana.mtn.billing.service;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.lang.String.format;

@Component
public class BillingScheduler {
    private static final Logger log = Logger.getLogger(BillingScheduler.class);
    private MotechSchedulerService schedulerService;
    private String jobKey;
    private String cron;

    @Autowired
    public BillingScheduler(MotechSchedulerService schedulerService,
                            @Value(value = "#{billingProperties['job.key']}") String jobKey,
                            @Value(value = "#{billingProperties['job.cron']}") String cron) {
        this.schedulerService = schedulerService;
        this.jobKey = jobKey;
        this.cron = cron;
    }

    public void startFor(BillingCycleRequest request) {
        String mobileNumber = request.getMobileNumber();
        String programName = request.programName();
        DateTime cycleStartDate = request.getCycleStartDate();
        Date startTime = cycleStartDate.monthOfYear().addToCopy(1).toDate();
        String cronJobExpression = format(cron, cycleStartDate.getDayOfMonth());

        String jobId = format("%s.%s.%s", jobKey, programName, mobileNumber);
        MotechEvent motechEvent = new MotechEvent(jobKey, new SchedulerParamsBuilder()
                .withJobId(jobId)
                .withExternalId(mobileNumber)
                .withProgram(programName)
                .params());

        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, startTime, null);
        schedulerService.scheduleJob(schedulableJob);
        log.info("Billing job scheduled for " + mobileNumber + "|" + programName);
    }


}
