package org.motechproject.ghana.mtn.repository;


import org.junit.Test;
import org.motechproject.ghana.mtn.BaseSpringTestContext;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.model.DayOfWeek;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class AllSubscriptionMessagesTest extends BaseSpringTestContext {
    @Autowired
    private AllProgramTypes allProgramTypes;
    @Autowired
    private AllProgramMessages allSubscriptionMessages;

    @Test
    public void shouldFindByProgramAndWeekAndDay(){
        String programKey = IProgramType.CHILDCARE;
        ProgramType type = new ProgramTypeBuilder().withProgramKey(programKey).build();
        addAndMarkForDeletion(allProgramTypes, type);

        Week week = new Week(12);
        ProgramMessage subscriptionMessage = new ProgramMessage(programKey,"content", new WeekAndDay(week, DayOfWeek.Friday));
        addAndMarkForDeletion(allSubscriptionMessages, subscriptionMessage);

        ProgramMessage dbSubscriptionMessage = allSubscriptionMessages.findBy(type, week, DayOfWeek.Friday);
        assertEquals(subscriptionMessage.getContent(),dbSubscriptionMessage.getContent());
    }
}
