package org.motechproject.ghana.mtn.billing.service;

import org.motechproject.scheduler.MotechSchedulerService;

import java.util.HashMap;

public class SchedulerParamsBuilder {
    public final static String PROGRAM = "Program";
    public final static String EXTERNAL_ID_KEY = "ExternalID";

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
