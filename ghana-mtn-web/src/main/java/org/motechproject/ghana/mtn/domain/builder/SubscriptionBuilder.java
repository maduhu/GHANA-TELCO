package org.motechproject.ghana.mtn.domain.builder;

import org.joda.time.DateTime;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;

public class SubscriptionBuilder extends Builder<Subscription> {
    private Subscriber subscriber;
    private SubscriptionType subscriptionType;
    private SubscriptionStatus status;
    private WeekAndDay startWeekAndDay;
    private DateTime registrationDate;

    public SubscriptionBuilder() {
        super(new Subscription());
    }

    public SubscriptionBuilder withSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
        return this;
    }

    public SubscriptionBuilder withType(SubscriptionType type) {
        this.subscriptionType = type;
        return this;
    }

    public SubscriptionBuilder withStatus(SubscriptionStatus status) {
        this.status = status;
        return this;
    }

    public SubscriptionBuilder withStartWeekAndDay(WeekAndDay week) {
        this.startWeekAndDay = week;
        return this;
    }

    public SubscriptionBuilder withRegistrationDate(DateTime dateTime) {
        this.registrationDate = dateTime;
        return this;
    }
}
