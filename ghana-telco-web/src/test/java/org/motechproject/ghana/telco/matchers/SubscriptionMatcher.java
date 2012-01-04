package org.motechproject.ghana.telco.matchers;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.mockito.ArgumentMatcher;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.domain.Subscriber;
import org.motechproject.ghana.telco.domain.Subscription;
import org.motechproject.ghana.telco.domain.SubscriptionStatus;
import org.motechproject.ghana.telco.domain.builder.SubscriptionBuilder;
import org.motechproject.ghana.telco.domain.vo.WeekAndDay;

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
                .append(subscription.getProgramType().getProgramKey(), toCompare.getProgramType().getProgramKey())
                .append(subscription.getStatus(), toCompare.getStatus()).isEquals()
                && subscription.getStartWeekAndDay().isSameAs(toCompare.getStartWeekAndDay());
    }
}
