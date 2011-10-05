package org.motechproject.ghana.mtn.repository;


import org.junit.Test;
import org.motechproject.ghana.mtn.BaseIntegrationTest;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.ProgramMessage;
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
    private AllProgramMessages allSubscriptionMessages;

    @Test
    public void shouldFindByProgramAndWeekAndDay(){
        String programName = "test-program";
        ProgramType type = new ProgramTypeBuilder().withProgramName(programName).build();
        addAndMarkForDeletion(allProgramTypes, type);

        Week week = new Week(12);
        ProgramMessage subscriptionMessage = new ProgramMessage(programName,"content", new WeekAndDay(week, Day.FRIDAY));
        addAndMarkForDeletion(allSubscriptionMessages, subscriptionMessage);

        ProgramMessage dbSubscriptionMessage = allSubscriptionMessages.findBy(type, week, Day.FRIDAY);
        assertEquals(subscriptionMessage.getContent(),dbSubscriptionMessage.getContent());
    }
}
