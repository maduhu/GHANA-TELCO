package org.motechproject.ghana.mtn.matchers;

import org.mockito.ArgumentMatcher;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.vo.Week;

public class SubscriptionMatcher extends ArgumentMatcher<Subscription> {
    private Subscription subscription;

    public SubscriptionMatcher(Subscriber subscriber, SubscriptionType type, SubscriptionStatus status, Week startWeek) {
        subscription = new SubscriptionBuilder().withSubscriber(subscriber).withType(type)
                .withStatus(status).withStartWeek(startWeek).build();
    }

    public SubscriptionMatcher(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public boolean matches(Object o) {
        Subscription toCompare = (Subscription) o;
        return subscription.getSubscriber().getNumber().equals(toCompare.getSubscriber().getNumber())
                && subscription.getSubscriptionType().getProgramName().equals(toCompare.getSubscriptionType().getProgramName())
                && subscription.getStatus().equals(toCompare.getStatus())
                && subscription.getStartWeek().is(toCompare.getStartWeek().getNumber());
    }
}
