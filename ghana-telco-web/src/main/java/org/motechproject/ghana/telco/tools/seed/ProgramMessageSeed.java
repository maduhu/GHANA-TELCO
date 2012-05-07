package org.motechproject.ghana.telco.tools.seed;

import org.motechproject.MotechException;
import org.motechproject.ghana.telco.domain.ProgramMessage;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.motechproject.ghana.telco.repository.AllProgramMessages;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.springframework.core.io.support.PropertiesLoaderUtils.loadAllProperties;

@Component
public class ProgramMessageSeed extends Seed {

    @Autowired
    private AllProgramMessages allSubscriptionMessages;

    @Autowired
    private AllMessageCampaigns messageCampaigns;

    @Override
    protected void load() {
        try {
            String[] languages = {"EN"};
            for (String language : languages) {
                Properties properties = loadAllProperties("programs/message_" + language + ".properties");
                saveProperties(properties, language);
            }
        } catch (Exception e) {
            throw new MotechException("Encountered exception while loading seed", e);
        }
    }

    private void saveProperties(Properties properties, String language) {

        RepeatingCampaignMessage pregnancyCampaignMessage = (RepeatingCampaignMessage) messageCampaigns.getCampaignMessageByMessageName(ProgramType.PREGNANCY, ProgramType.PREGNANCY);
        RepeatingCampaignMessage childCareCampaignMessage = (RepeatingCampaignMessage) messageCampaigns.getCampaignMessageByMessageName(ProgramType.CHILDCARE, ProgramType.CHILDCARE);
        Map<String, String> pregnancyDayMap = createDayMap(pregnancyCampaignMessage);
        Map<String, String> childCareDayMap = createDayMap(childCareCampaignMessage);

        for (Object key : properties.keySet()) {
            String keyStr = (String) key;
            String messageContent = (String) properties.get(key);
            String messageContentKey = null;
            String[] tokens = keyStr.split("-");

            String weekDay = tokens[2];
            if (tokens[0].equals(ProgramType.PREGNANCY)) {
                messageContentKey = keyStr.replace(weekDay, pregnancyDayMap.get(weekDay));
            } else if (tokens[0].equals(ProgramType.CHILDCARE)) {
                messageContentKey = keyStr.replace(weekDay, childCareDayMap.get(weekDay));
            }
            allSubscriptionMessages.add(new ProgramMessage(messageContentKey, tokens[0], messageContent));
        }
    }

    private Map<String, String> createDayMap(RepeatingCampaignMessage campaignMessage) {
        List<DayOfWeek> weekDayList = campaignMessage.weekDaysApplicable();
        Map<String, String> weekDayMap = new HashMap<String, String>();
        int count = 0;
        for (DayOfWeek dayOfWeek : weekDayList) {
            weekDayMap.put("{d" + (++count) + "}", dayOfWeek.name());
        }
        return weekDayMap;
    }
}
