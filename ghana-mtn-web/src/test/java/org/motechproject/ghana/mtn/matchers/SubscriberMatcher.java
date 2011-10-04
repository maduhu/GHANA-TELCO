package org.motechproject.ghana.mtn.matchers;

import org.mockito.ArgumentMatcher;
import org.motechproject.ghana.mtn.domain.Subscriber;

public class SubscriberMatcher extends ArgumentMatcher<Subscriber> {
    private String number;

    public SubscriberMatcher(String number) {
        this.number = number;
    }

    public SubscriberMatcher(Subscriber subscriber) {
        this.number = subscriber.getNumber();
    }

    @Override
    public boolean matches(Object o) {
        Subscriber subscriber = (Subscriber) o;
        return number.equals(subscriber.getNumber());
    }
}
