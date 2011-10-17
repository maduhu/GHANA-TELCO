package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.repository.AllProgramMessages;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramMessageSeed extends Seed {
    public static final String DUMMY = "message content for ";
    @Autowired
    private AllProgramMessages allSubscriptionMessages;
    @Autowired
    private AllProgramTypes allProgramTypes;

    @Override
    protected void load() {
        loadPregnancyCareMessages();
        loadChildCareMessages();
    }

    private void loadPregnancyCareMessages() {
        ProgramType programType = allProgramTypes.findByCampaignShortCode("P");
        persistMessagesFor(programType);
    }

    private void loadChildCareMessages() {
        ProgramType programType = allProgramTypes.findByCampaignShortCode("C");
        persistMessagesFor(programType);
    }

    private void persistMessagesFor(ProgramType programType) {
        for (int i = programType.getMinWeek(); i <= programType.getMaxWeek(); i++) {
            String programName = programType.getProgramName();
            Week week = new Week(i);
            allSubscriptionMessages.add(new ProgramMessage(programName, DUMMY + week + "-" + Day.MONDAY.name(), new WeekAndDay(week, Day.MONDAY)));
            allSubscriptionMessages.add(new ProgramMessage(programName, DUMMY + week + "-" + Day.WEDNESDAY.name(), new WeekAndDay(week, Day.WEDNESDAY)));
            allSubscriptionMessages.add(new ProgramMessage(programName, DUMMY + week + "-" + Day.FRIDAY.name(), new WeekAndDay(week, Day.FRIDAY)));
        }
    }

}
