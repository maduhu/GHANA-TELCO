package org.motechproject.ghana.mtn.testbuilders;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;

public class TestSubscription {

    public static Subscription with(Subscriber subscriber, SubscriptionType type, DateTime dateTime, WeekAndDay weekAndDay) {
        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setStartWeekAndDay(weekAndDay);
        subscription.setSubscriptionType(type);
        subscription.setRegistrationDate(dateTime);
        return subscription;
    }
}
