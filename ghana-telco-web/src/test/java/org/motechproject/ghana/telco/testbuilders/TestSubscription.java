package org.motechproject.ghana.telco.testbuilders;

import org.joda.time.DateTime;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.Subscriber;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.domain.vo.WeekAndDay;

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
