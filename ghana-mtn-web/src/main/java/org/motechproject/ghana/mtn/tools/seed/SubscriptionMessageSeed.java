package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.domain.SubscriptionMessage;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.repository.AllSubscriptionMessages;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMessageSeed extends Seed {
    @Autowired
    private AllSubscriptionMessages allSubscriptionMessages;
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
            allSubscriptionMessages.add(new SubscriptionMessage(programName, week + "-" + Day.MONDAY.name(), new WeekAndDay(week, Day.MONDAY)));
            allSubscriptionMessages.add(new SubscriptionMessage(programName, week + "-" + Day.WEDNESDAY.name(), new WeekAndDay(week, Day.WEDNESDAY)));
            allSubscriptionMessages.add(new SubscriptionMessage(programName, week + "-" + Day.FRIDAY.name(),  new WeekAndDay(week, Day.FRIDAY)));
        }
    }

}
