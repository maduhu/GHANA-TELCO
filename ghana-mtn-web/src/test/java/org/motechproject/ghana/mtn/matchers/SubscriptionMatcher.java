package org.motechproject.ghana.mtn.matchers;

import org.mockito.ArgumentMatcher;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;

public class SubscriptionMatcher extends ArgumentMatcher<Subscription> {
    private Subscription subscription;

    public SubscriptionMatcher(Subscription subscription) {
        this.subscription = subscription;
    }

    public SubscriptionMatcher(Subscriber subscriber, SubscriptionType type, SubscriptionStatus status, WeekAndDay startWeekAndDay) {
        subscription = new SubscriptionBuilder().withSubscriber(subscriber).withType(type)
                .withStatus(status).withStartWeekAndDay(startWeekAndDay).build();
    }

    @Override
    public boolean matches(Object o) {
        Subscription toCompare = (Subscription) o;
        return subscription.getSubscriber().getNumber().equals(toCompare.getSubscriber().getNumber())
                && subscription.getSubscriptionType().getProgramName().equals(toCompare.getSubscriptionType().getProgramName())
                && subscription.getStatus().equals(toCompare.getStatus())
                && subscription.getStartWeekAndDay().isSameAs(toCompare.getStartWeekAndDay());
    }
}
