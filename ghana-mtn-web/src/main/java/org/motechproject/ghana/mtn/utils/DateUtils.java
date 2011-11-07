package org.motechproject.ghana.mtn.utils;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.util.DateUtil;

//TODO: Should use DateUtils class which is in the platform as it also takes care of locale
public class DateUtils {

    public DateTime now() {
        return DateUtil.now();
    }

    public Day today() {
        String day = now().dayOfWeek().getAsText();
        return Day.valueOf(day.toUpperCase());
    }

    public Day day(DateTime date) {
        return Day.valueOf(date.dayOfWeek().getAsText().toUpperCase());
    }

    public String dayWithOrdinal(int date) {
        return date + ordinal(date);
    }

    private String ordinal(int date) {
        int hunRem = date % 100;
        int tenRem = date % 10;
        
        if (hunRem - tenRem == 10) {
            return "th";
        }
        switch (tenRem) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
