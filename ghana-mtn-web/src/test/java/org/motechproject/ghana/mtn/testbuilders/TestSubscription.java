package org.motechproject.ghana.mtn.testbuilders;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;

public class TestSubscription {

    public static Subscription with(Subscriber subscriber, ProgramType type, DateTime dateTime, WeekAndDay weekAndDay) {
        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setStartWeekAndDay(weekAndDay);
        subscription.setProgramType(type);
        subscription.setRegistrationDate(dateTime);
        return subscription;
    }
}
