package org.motechproject.ghana.mtn.matchers;

import org.joda.time.DateTime;
import org.mockito.ArgumentMatcher;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.vo.Week;

public class SubscriptionMatcher extends ArgumentMatcher<Subscription> {
    private Subscriber subscriber;
    private SubscriptionType type;
    private SubscriptionStatus status;
    private Week startWeek;

    public SubscriptionMatcher(Subscriber subscriber, SubscriptionType type, SubscriptionStatus status, Week startWeek) {
        this.subscriber = subscriber;
        this.type = type;
        this.status = status;
        this.startWeek = startWeek;
    }

    @Override
    public boolean matches(Object o) {
        Subscription subscription = (Subscription) o;
        return subscriber.getNumber().equals(subscription.getSubscriber().getNumber())
                && type.equals(subscription.getType())
                && status.equals(subscription.getStatus())
                && startWeek.is(subscription.getStartWeek().number());
    }
}
