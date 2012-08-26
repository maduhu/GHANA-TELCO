package org.motechproject.ghana.telco.domain;

import org.joda.time.DateTime;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.joda.time.DateTimeConstants.SUNDAY;
import static org.motechproject.util.DateUtil.*;

@Component
public class ProgramMessageCycle {

    @Autowired
    private AllMessageCampaigns allMessageCampaigns;

    public DateTime nearestCycleDate(Subscription subscription) {
        DateTime fromDate = subscription.getRegistrationDate();
        List<DayOfWeek> applicableDays = allMessageCampaigns.getApplicableDaysForRepeatingCampaign(subscription.programKey(), subscription.programKey());
        return nextApplicableWeekDay(fromDate, applicableDays);
    }
}
