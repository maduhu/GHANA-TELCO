package org.motechproject.ghana.mtn.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.motechproject.ghana.mtn.domain.SubscriptionType;

public class SubscriptionTypeMatcher extends BaseMatcher<SubscriptionType> {

    private SubscriptionType subscriptionType;

    public SubscriptionTypeMatcher(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    @Override
    public boolean matches(Object o) {
        SubscriptionType subscriptionType = (SubscriptionType) o;
        return subscriptionType.getProgramName().equals(this.subscriptionType.getProgramName())
                && subscriptionType.getShortCodes().equals(this.subscriptionType.getShortCodes())
                && subscriptionType.getMinWeek().equals(this.subscriptionType.getMinWeek())
                && subscriptionType.getMaxWeek().equals(this.subscriptionType.getMaxWeek());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(subscriptionType.toString());
    }
}
