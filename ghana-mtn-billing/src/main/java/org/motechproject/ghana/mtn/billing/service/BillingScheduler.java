package org.motechproject.ghana.mtn.billing.service;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.DefaultedBillingRequest;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.valueobjects.WallTimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.valueOf;
import static java.lang.String.format;

@Component
public class BillingScheduler {
    private static final Logger log = Logger.getLogger(BillingScheduler.class);
    public static final String MONTHLY_BILLING_SCHEDULE_SUBJECT = "org.motechproject.ghana.mtn.service.billingschedule";
    public static final String DEFAULTED_DAILY_SCHEDULE = "org.motechproject.ghana.mtn.service.defaultedBillingDailySchedule";
    public static final String DEFAULTED_WEEKLY_SCHEDULE = "org.motechproject.ghana.mtn.service.defaultedBillingWeeklySchedule";
    public final static String PROGRAM_KEY = "Program";

    public final static String EXTERNAL_ID_KEY = "ExternalID";
    private MotechSchedulerService schedulerService;
    private String cron;

    @Autowired
    public BillingScheduler(MotechSchedulerService schedulerService,
                            @Value(value = "#{billingProperties['job.cron']}") String cron) {
        this.schedulerService = schedulerService;
        this.cron = cron;
    }

    public void startFor(BillingCycleRequest request) {
        String mobileNumber = request.getMobileNumber();
        String programKey = request.programKey();
        DateTime cycleStartDate = request.getCycleStartDate();
        Date startTime = cycleStartDate.monthOfYear().addToCopy(1).toDate();
        String cronJobExpression = format(cron, cycleStartDate.getDayOfMonth());

        String jobId = jobId(mobileNumber, programKey);
        MotechEvent motechEvent = new MotechEvent(MONTHLY_BILLING_SCHEDULE_SUBJECT, new SchedulerParamsBuilder()
                .withJobId(jobId)
                .withExternalId(mobileNumber)
                .withProgram(programKey)
                .params());

        CronSchedulableJob schedulableJob = new CronSchedulableJob(motechEvent, cronJobExpression, startTime, null);
        schedulerService.scheduleJob(schedulableJob);
        log.info("Billing job scheduled for [" + mobileNumber + "|" + programKey + "|" + startTime + "]");
    }

    public void stopFor(BillingCycleRequest request) {
        String mobileNumber = request.getMobileNumber();
        String programName = request.programKey();
        String jobId = jobId(mobileNumber, programName);

        schedulerService.unscheduleJob(MONTHLY_BILLING_SCHEDULE_SUBJECT, jobId);
        log.info("Billing job unscheduled for [" + mobileNumber + "|" + programName + "]");
    }

    public void stop(DefaultedBillingRequest request) {
        String mobileNumber = request.getMobileNumber();
        String programName = request.programKey();
        String jobId = jobId(mobileNumber, programName);

        schedulerService.unscheduleJob(defaultedBillingSubjectMap().get(request.getFrequency()), jobId);
        log.info("Billing defaulted job unscheduled for [" + mobileNumber + "|" + programName + "]");
    }

    public void startDefaultedBillingSchedule(DefaultedBillingRequest request) {
        String mobileNumber = request.getMobileNumber();
        String programKey = request.programKey();
        Date startTime = request.getCycleStartDate().toDate();

        String subject = defaultedBillingSubjectMap().get(request.getFrequency());
        MotechEvent motechEvent = new MotechEvent(subject, new SchedulerParamsBuilder()
                .withJobId(jobId(mobileNumber, programKey))
                .withExternalId(mobileNumber)
                .withProgram(programKey)
                .params());

        WallTimeUnit unit = request.getFrequency();
        RepeatingSchedulableJob repeatingSchedulableJob = new RepeatingSchedulableJob(motechEvent,
                startTime, request.getCycleEndDate().toDate(), getRepeatingIntervalForPeriod(unit.toPeriod(unit.days)));
        schedulerService.scheduleRepeatingJob(repeatingSchedulableJob);
        log.info("Defaulted Billing job scheduled for [" + mobileNumber + "|" + programKey + "|" + startTime + "]");
    }

    private Map<WallTimeUnit, String> defaultedBillingSubjectMap() {
        Map<WallTimeUnit, String> map = new HashMap<WallTimeUnit, String>();
        map.put(WallTimeUnit.Day, DEFAULTED_DAILY_SCHEDULE);
        map.put(WallTimeUnit.Week, DEFAULTED_WEEKLY_SCHEDULE);
        return map;
    }

    private long getRepeatingIntervalForPeriod(Period period) {
        return valueOf(period.toStandardSeconds().
                getSeconds()) * 1000L;
    }

    private String jobId(String mobileNumber, String programName) {
        return format("%s.%s", programName, mobileNumber);
    }
}
