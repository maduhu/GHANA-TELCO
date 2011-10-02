package org.motechproject.ghana.mtn.repository;


import org.junit.Test;
import org.motechproject.ghana.mtn.domain.SubscriptionMessage;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.motechproject.ghana.mtn.domain.builder.SubscriptionTypeBuilder;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class AllSubscriptionMessagesTest extends BaseRepositoryTest{
    @Autowired
    private AllSubscriptionTypes allSubscriptionTypes;
    @Autowired
    private AllSubscriptionMessages allSubscriptionMessages;

    @Test
    public void shouldFindByProgramAndWeekAndDay(){
        String programName = "test-program";
        SubscriptionType type = new SubscriptionTypeBuilder().withProgramName(programName).build();
        allSubscriptionTypes.add(type);
        markForDeletion(type);

        Week week = new Week(12);
        SubscriptionMessage subscriptionMessage = new SubscriptionMessage(programName,"content", new WeekAndDay(week, Day.FRIDAY));
        allSubscriptionMessages.add(subscriptionMessage);
        markForDeletion(subscriptionMessage);

        SubscriptionMessage dbSubscriptionMessage = allSubscriptionMessages.findBy(type, week, Day.FRIDAY);
        assertEquals(subscriptionMessage.getContent(),dbSubscriptionMessage.getContent());
    }
}
