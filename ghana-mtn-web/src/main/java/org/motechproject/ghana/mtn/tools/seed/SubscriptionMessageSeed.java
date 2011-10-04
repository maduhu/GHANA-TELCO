package org.motechproject.ghana.mtn.tools.seed;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.domain.SubscriptionMessage;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.repository.AllSubscriptionMessages;
import org.motechproject.ghana.mtn.repository.AllSubscriptionTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMessageSeed extends Seed {
    @Autowired
    private AllSubscriptionMessages allSubscriptionMessages;
    @Autowired
    private AllSubscriptionTypes allSubscriptionTypes;

    @Override
    protected void load() {
        loadPregnancyCareMessages();
        loadChildCareMessages();
    }

    private void loadPregnancyCareMessages() {
        SubscriptionType subscriptionType = allSubscriptionTypes.findByCampaignShortCode("P");
        persistMessagesFor(subscriptionType);
    }

    private void loadChildCareMessages() {
        SubscriptionType subscriptionType = allSubscriptionTypes.findByCampaignShortCode("C");
        persistMessagesFor(subscriptionType);
    }

    private void persistMessagesFor(SubscriptionType subscriptionType) {
        for (int i = subscriptionType.getMinWeek(); i <= subscriptionType.getMaxWeek(); i++) {
            String programName = subscriptionType.getProgramName();
            Week week = new Week(i);
            allSubscriptionMessages.add(new SubscriptionMessage(programName, week + "-" + Day.MONDAY.name(), new WeekAndDay(week, Day.MONDAY)));
            allSubscriptionMessages.add(new SubscriptionMessage(programName, week + "-" + Day.WEDNESDAY.name(), new WeekAndDay(week, Day.WEDNESDAY)));
            allSubscriptionMessages.add(new SubscriptionMessage(programName, week + "-" + Day.FRIDAY.name(),  new WeekAndDay(week, Day.FRIDAY)));
            allSubscriptionMessages.add(new SubscriptionMessage(programName, week + "-" + Day.SUNDAY.name(), new WeekAndDay(week, Day.SUNDAY)));
        }
    }

}
