package org.motechproject.ghana.mtn.utils;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.springframework.stereotype.Component;

public class DateUtils {

    public DateTime now() {
        return DateTime.now();
    }

    public Day currentDay() {
        String day = DateTime.now().dayOfWeek().getAsText();
        return Day.valueOf(day.toUpperCase());
    }
}
