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
    private AllProgramMessages allProgramMessages;

    @Autowired
    private AllMessageCampaigns messageCampaigns;

    @Override
    protected void load() {
        allProgramMessages.removeAll();
        try {
            String[] languages = {"EN"};
            for (String language : languages) {
                Properties properties = loadAllProperties("programs/message_" + language + ".properties");
                saveProperties(properties);
            }
        } catch (Exception e) {
            throw new MotechException("Encountered exception while loading seed", e);
        }
    }

    private void saveProperties(Properties properties) {

        List<RepeatingCampaignMessage> allCampaignMessages = getAllCampaignMessages();
        Map<String, Map<String,String>> campaignWeekDayMap = createMessageWeekDayMapFrom(allCampaignMessages);

        for (Object key : properties.keySet()) {
            String keyStr = (String) key;
            String messageContent = (String) properties.get(key);
            String messageContentKey = null;
            String[] tokens = keyStr.split("-");

            String weekDay = tokens[2];
            messageContentKey = keyStr.replace(weekDay, campaignWeekDayMap.get(tokens[0]).get(weekDay));

            allProgramMessages.add(new ProgramMessage(messageContentKey, tokens[0], messageContent));
        }
    }

    private List<RepeatingCampaignMessage> getAllCampaignMessages() {
        List<RepeatingCampaignMessage> messages = messageCampaigns.get(ProgramType.CHILDCARE).messages();
        messages.addAll(messageCampaigns.get(ProgramType.PREGNANCY).messages());
        return messages;
    }

    private Map<String, Map<String, String>> createMessageWeekDayMapFrom(List<RepeatingCampaignMessage> campaignMessages) {
        Map<String, Map<String, String>> campaignMessageWeekdayMap = new HashMap<String, Map<String, String>>();
        for (RepeatingCampaignMessage campaignMessage : campaignMessages) {
            List<DayOfWeek> weekDayList = campaignMessage.weekDaysApplicable();
            Map<String, String> weekDayMap = new HashMap<String, String>();
            int count = 0;
            for (DayOfWeek dayOfWeek : weekDayList) {
                weekDayMap.put("{d" + (++count) + "}", dayOfWeek.name());
            }
            campaignMessageWeekdayMap.put(campaignMessage.name(), weekDayMap);
        }

        return campaignMessageWeekdayMap;
    }
}
