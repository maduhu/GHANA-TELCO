package org.motechproject.ghana.mtn.domain;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.joda.time.DateTimeConstants.*;

public class ProgramMessageCycle {

    static List<Integer> APPLICABLE_DAYS = new ArrayList<Integer>();
    static {
        APPLICABLE_DAYS.add(MONDAY);
        APPLICABLE_DAYS.add(WEDNESDAY);
        APPLICABLE_DAYS.add(FRIDAY);
    }

    public DateTime nearestCycleDate(DateTime fromDate) {
        return APPLICABLE_DAYS.contains(fromDate.getDayOfWeek()) ?  calculateNextPossible(fromDate.dayOfMonth().addToCopy(1)) : calculateNextPossible(fromDate);
    }

    private DateTime calculateNextPossible(DateTime fromDate) {
        int dayOfWeek = fromDate.getDayOfWeek();
        int noOfDaysToNearestCycleDate  = 0;
        for (int currentDayOfWeek = dayOfWeek, dayCount = 0; dayCount <= SUNDAY ; dayCount++) {
            if(APPLICABLE_DAYS.contains(currentDayOfWeek)) {
               noOfDaysToNearestCycleDate = dayCount;
               break;
            }
            if (currentDayOfWeek == SUNDAY) currentDayOfWeek = 1;
            else currentDayOfWeek++;
        }
        return fromDate.dayOfMonth().addToCopy(noOfDaysToNearestCycleDate);
    }
}
