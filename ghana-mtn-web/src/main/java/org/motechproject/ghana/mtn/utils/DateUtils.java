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

    public DateTime startOfDay(DateTime dateTime) {
        return dateTime.withTime(0, 0, 0, 0);
    }

    public Day day(DateTime date) {
        return Day.valueOf(date.dayOfWeek().getAsText().toUpperCase());
    }
}
