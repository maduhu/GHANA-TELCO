package org.motechproject.ghana.mtn.billing.service;

import org.motechproject.scheduler.MotechSchedulerService;

import java.util.HashMap;

import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.EXTERNAL_ID_KEY;
import static org.motechproject.ghana.mtn.billing.service.BillingScheduler.PROGRAM;

public class SchedulerParamsBuilder {

    private HashMap<String, Object> params = new HashMap<String, Object>();

    public HashMap<String, Object> params() {
        return params;
    }

    public SchedulerParamsBuilder withJobId(String id) {
        params.put(MotechSchedulerService.JOB_ID_KEY, id);
        return this;
    }

    public SchedulerParamsBuilder withExternalId(String id) {
        params.put(EXTERNAL_ID_KEY, id);
        return this;
    }

    public SchedulerParamsBuilder withProgram(String program) {
        params.put(PROGRAM, program);
        return this;
    }
}
