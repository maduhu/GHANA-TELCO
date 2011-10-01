package org.motechproject.ghana.mtn.utils;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class DateUtils {

    public DateTime now() {
        return DateTime.now();
    }
}
