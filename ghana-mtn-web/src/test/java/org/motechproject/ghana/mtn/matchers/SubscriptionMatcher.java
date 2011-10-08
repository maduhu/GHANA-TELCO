package org.motechproject.ghana.mtn.matchers;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.mockito.ArgumentMatcher;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;

public class SubscriptionMatcher extends ArgumentMatcher<Subscription> {
    private Subscription subscription;

    public SubscriptionMatcher(Subscription subscription) {
        this.subscription = subscription;
    }

    public SubscriptionMatcher(Subscriber subscriber, ProgramType type, SubscriptionStatus status, WeekAndDay startWeekAndDay) {
        subscription = new SubscriptionBuilder().withSubscriber(subscriber).withType(type)
                .withStatus(status).withStartWeekAndDay(startWeekAndDay).build();
    }

    @Override
    public boolean matches(Object o) {
        Subscription toCompare = (Subscription) o;
        return new EqualsBuilder()
                .append(subscription.getSubscriber().getNumber(), toCompare.getSubscriber().getNumber())
                .append(subscription.getProgramType().getProgramName(), toCompare.getProgramType().getProgramName())
                .append(subscription.getStatus(), toCompare.getStatus()).isEquals()
                && subscription.getStartWeekAndDay().isSameAs(toCompare.getStartWeekAndDay());
    }
}
