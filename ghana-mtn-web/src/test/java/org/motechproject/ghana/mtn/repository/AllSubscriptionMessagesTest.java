package org.motechproject.ghana.mtn.repository;


import org.junit.Test;
import org.motechproject.ghana.mtn.BaseIntegrationTest;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.SubscriptionMessage;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class AllSubscriptionMessagesTest extends BaseIntegrationTest {
    @Autowired
    private AllProgramTypes allProgramTypes;
    @Autowired
    private AllSubscriptionMessages allSubscriptionMessages;

    @Test
    public void shouldFindByProgramAndWeekAndDay(){
        String programName = "test-program";
        ProgramType type = new ProgramTypeBuilder().withProgramName(programName).build();
        allProgramTypes.add(type);
        markForDeletion(type);

        Week week = new Week(12);
        SubscriptionMessage subscriptionMessage = new SubscriptionMessage(programName,"content", new WeekAndDay(week, Day.FRIDAY));
        allSubscriptionMessages.add(subscriptionMessage);
        markForDeletion(subscriptionMessage);

        SubscriptionMessage dbSubscriptionMessage = allSubscriptionMessages.findBy(type, week, Day.FRIDAY);
        assertEquals(subscriptionMessage.getContent(),dbSubscriptionMessage.getContent());
    }
}
