package org.motechproject.ghana.mtn.process;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.billing.service.SchedulerParamsBuilder;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.utils.DateUtils;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.lang.String.format;

@Component
public class RollOverWaitSchedule {
    public static final String ROLLOVER_WAIT_SCHEDULE = "org.motechproject.ghana.mtn.service.rollOverWait";
    private MotechSchedulerService scheduledService;
    private final Logger log = Logger.getLogger(RollOverWaitSchedule.class);

    @Autowired
    public RollOverWaitSchedule(MotechSchedulerService scheduledService) {
        this.scheduledService = scheduledService;
    }

    public void startScheduleWaitFor(Subscription subscription) {
        String mobileNumber = subscription.subscriberNumber();
        String programKey = subscription.programKey();
        Date startTime = new DateUtils().now().dayOfMonth().addToCopy(3).toDate();

        String jobId = jobId(mobileNumber);
        MotechEvent motechEvent = new MotechEvent(ROLLOVER_WAIT_SCHEDULE, new SchedulerParamsBuilder()
                .withJobId(jobId)
                .withExternalId(mobileNumber)
                .withProgram(programKey)
                .params());

        RunOnceSchedulableJob job = new RunOnceSchedulableJob(motechEvent, startTime);
        scheduledService.scheduleRunOnceJob(job);
        log.info("RollOverWait job scheduled for [" + mobileNumber + "|" + programKey + "|" + startTime + "]");
    }

    private String jobId(String mobileNumber) {
        return format("RollOverSchedule.%s", mobileNumber);
    }
}
