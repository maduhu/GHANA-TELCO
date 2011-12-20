package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.repository.AllProgramMessages;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.motechproject.model.DayOfWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

import static java.lang.String.format;

@Component
public class ProgramMessageSeed extends Seed {
    public static final String DUMMY = "message content for ";

    @Autowired
    private  AllProgramMessages allSubscriptionMessages;

    @Autowired
    private AllProgramTypes allProgramTypes;

    @Autowired
    @Qualifier("messageContentProperties")
    private Properties messageContentProperty;

    private static final String CHILDCARE_PATH = "childcare.seed.file.path";
    private static final String PREGNANCY_PATH = "pregnancy.seed.file.path";

    @Override
    protected void load() {
       // allSubscriptionMessages.getAll().clear();
        loadPregnancyCareMessages();
        loadChildCareMessages();
    }

    private void loadPregnancyCareMessages() {
        ProgramType programType = allProgramTypes.findByCampaignShortCode("P");
        persistMessagesFor(programType, getPathFor(PREGNANCY_PATH));
    }

    private void loadChildCareMessages() {
        ProgramType programType = allProgramTypes.findByCampaignShortCode("C");
        persistMessagesFor(programType, getPathFor(CHILDCARE_PATH));
    }

    private void persistMessagesFor(ProgramType programType,String fileName) {
        String program = programType.getProgramKey().toLowerCase();
        try{
        FileReader in=new FileReader(new ClassPathResource(fileName).getFile());
        BufferedReader br=new BufferedReader(in);
        String line;
        String []contents;
        while((line=br.readLine())!=null) {
            contents=line.split("\t");
            String programKey = programType.getProgramKey();
            String messageContent=contents[0];
            Integer weekNumber=Integer.parseInt(contents[1]);
            Week week = new Week(weekNumber);
            allSubscriptionMessages.add(new ProgramMessage(format(program + "-calendar-week-%s-Monday", weekNumber),programKey,messageContent, new WeekAndDay(week, DayOfWeek.Monday)));
            allSubscriptionMessages.add(new ProgramMessage(format(program + "-calendar-week-%s-Wednesday", weekNumber), programKey,messageContent, new WeekAndDay(week, DayOfWeek.Wednesday)));
            allSubscriptionMessages.add(new ProgramMessage(format(program + "-calendar-week-%s-Friday", weekNumber), programKey, messageContent, new WeekAndDay(week, DayOfWeek.Friday)));

        }
        br.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private String getPathFor(String path) {
        return messageContentProperty.getProperty(path);
    }
}


