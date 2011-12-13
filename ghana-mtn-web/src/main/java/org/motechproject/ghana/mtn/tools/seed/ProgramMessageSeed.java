package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.repository.AllProgramMessages;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.motechproject.model.DayOfWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

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
        String program = programType.getProgramKey().toLowerCase();
        for (int i = programType.getMinWeek(); i <= programType.getMaxWeek(); i++) {
            String programKey = programType.getProgramKey();
            Week week = new Week(i);
            allSubscriptionMessages.add(new ProgramMessage(format(program + "-calendar-week-%s-Monday", i),programKey, DUMMY + week + "-" + DayOfWeek.Monday.name(), new WeekAndDay(week, DayOfWeek.Monday)));
            allSubscriptionMessages.add(new ProgramMessage(format(program + "-calendar-week-%s-Wednesday", i), programKey, DUMMY + week + "-" + DayOfWeek.Wednesday.name(), new WeekAndDay(week, DayOfWeek.Wednesday)));
            allSubscriptionMessages.add(new ProgramMessage(format(program + "-calendar-week-%s-Friday", i), programKey, DUMMY + week + "-" + DayOfWeek.Friday.name(), new WeekAndDay(week, DayOfWeek.Friday)));
        }
    }

}
