package org.motechproject.ghana.mtn.listener;

import org.motechproject.ghana.mtn.service.listener.PregnancyMessageListener;
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
        beans.put(PregnancyMessageListener.class.getName(), webApplicationContext.getBean(PregnancyMessageListener.class));
        return beans;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}