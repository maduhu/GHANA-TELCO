package org.motechproject.ghana.telco.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.telco.BaseSpringTestContext;
import org.motechproject.ghana.telco.domain.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.ghana.telco.domain.AppConfig.WINDOW_START_TIME_KEY;

public class AllAppConfigsTest extends BaseSpringTestContext {

    @Autowired
    private AllAppConfigs allAppConfigs;

    @Before
    public void setUp() {
        addAndMarkForDeletion(allAppConfigs, new AppConfig(WINDOW_START_TIME_KEY, "10:30"));
    }

    @Test
    public void shouldGetConfigByKey() {
        AppConfig startTimeConfig = allAppConfigs.findByKey(WINDOW_START_TIME_KEY);
        assertThat(startTimeConfig.value().toString(), is(equalTo("10:30")));
    }
}
