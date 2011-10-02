package org.motechproject.ghana.mtn.tools.seed;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupSeedData {
    private final static Logger log = Logger.getLogger(SetupSeedData.class);

    public static final String APPLICATION_CONTEXT_XML = "seed/applicationContext-tools.xml";

    public static void main(String[] args) {
        log.info("Seed loading: START");
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        SeedLoader seedLoader = (SeedLoader) context.getBean("seedLoader");
        seedLoader.load();
        ((ClassPathXmlApplicationContext) context).close();
        log.info("Seed loading: END");
    }
}
