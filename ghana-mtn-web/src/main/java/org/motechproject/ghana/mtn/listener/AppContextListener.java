package org.motechproject.ghana.mtn.listener;

import org.motechproject.ghana.mtn.eventhandler.BillingEventHandler;
import org.motechproject.ghana.mtn.eventhandler.ProgramMessageEventHandler;
import org.motechproject.server.event.annotations.EventAnnotationBeanPostProcessor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            EventAnnotationBeanPostProcessor.registerHandlers(getListeners(sce.getServletContext()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    private HashMap<String, Object> getListeners(ServletContext servletContext) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        HashMap<String, Object> beans = new HashMap<String, Object>();
        beans.put(ProgramMessageEventHandler.class.getName(), webApplicationContext.getBean(ProgramMessageEventHandler.class));
        beans.put(BillingEventHandler.class.getName(), webApplicationContext.getBean(BillingEventHandler.class));
        return beans;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}