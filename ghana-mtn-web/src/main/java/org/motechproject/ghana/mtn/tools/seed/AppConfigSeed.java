package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.domain.AppConfig;
import org.motechproject.ghana.mtn.repository.AllAppConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.ghana.mtn.domain.AppConfig.WINDOW_END_TIME_KEY;
import static org.motechproject.ghana.mtn.domain.AppConfig.WINDOW_START_TIME_KEY;

@Component
public class AppConfigSeed extends Seed {
    @Autowired
    private AllAppConfigs allAppConfigs;

    @Override
    protected void load() {
        allAppConfigs.add(new AppConfig(WINDOW_START_TIME_KEY, "10:30"));
        allAppConfigs.add(new AppConfig(WINDOW_END_TIME_KEY, "11:30"));
    }
}
