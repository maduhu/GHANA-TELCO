package org.motechproject.ghana.mtn.domain;

import org.joda.time.DateTime;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.joda.time.DateTimeConstants.SUNDAY;

@Component
public class ProgramMessageCycle {

    @Autowired
    private AllMessageCampaigns allMessageCampaigns;

    public DateTime nearestCycleDate(Subscription subscription) {
        DateTime fromDate = subscription.getRegistrationDate();
        List<DayOfWeek> applicableDays = allMessageCampaigns.getApplicableDaysForRepeatingCampaign(subscription.programKey(), getMessageName(subscription.programKey()));
        return isApplicableDay(fromDate.getDayOfWeek(), applicableDays) ? calculateNextPossible(fromDate.dayOfMonth().addToCopy(1), applicableDays) : calculateNextPossible(fromDate, applicableDays);
    }

    private boolean isApplicableDay(int dayOfWeek, List<DayOfWeek> applicableDays) {
        for (DayOfWeek applicableDay : applicableDays) {
            if (dayOfWeek == applicableDay.getValue()) {
                return true;
            }
        }
        return false;
    }

    private String getMessageName(String programKey) {
        if (programKey.equals(ProgramType.PREGNANCY))
            return "Pregnancy Message";
        return "ChildCare Message";
    }

    private DateTime calculateNextPossible(DateTime fromDate, List<DayOfWeek> applicableDays) {
        int dayOfWeek = fromDate.getDayOfWeek();
        int noOfDaysToNearestCycleDate = 0;
        for (int currentDayOfWeek = dayOfWeek, dayCount = 0; dayCount <= SUNDAY; dayCount++) {
            if (isApplicableDay(currentDayOfWeek, applicableDays)) {
                noOfDaysToNearestCycleDate = dayCount;
                break;
            }
            if (currentDayOfWeek == SUNDAY) currentDayOfWeek = 1;
            else currentDayOfWeek++;
        }
        return fromDate.dayOfMonth().addToCopy(noOfDaysToNearestCycleDate);
    }
}
