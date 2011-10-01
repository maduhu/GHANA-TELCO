package org.motechproject.ghana.mtn.testbuilders;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.vo.Week;

public class TestSubscription {

    public static Subscription with(Subscriber subscriber, SubscriptionType type, DateTime dateTime, Week week) {
        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setStartWeek(week);
        subscription.setSubscriptionType(type);
        subscription.setRegistrationDate(dateTime);
        return subscription;
    }
}
