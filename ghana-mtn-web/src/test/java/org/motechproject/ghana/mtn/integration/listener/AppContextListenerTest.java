package org.motechproject.ghana.mtn.integration.listener;

import org.junit.Test;
import org.motechproject.context.Context;
import org.motechproject.ghana.mtn.BaseSpringTestContext;
import org.motechproject.ghana.mtn.eventhandler.ProgramMessageEventHandler;
import org.motechproject.ghana.mtn.listener.AppContextListener;
import org.motechproject.server.event.EventListener;
import org.motechproject.server.event.annotations.MotechListenerEventProxy;

import javax.servlet.ServletContextEvent;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;


public class AppContextListenerTest extends BaseSpringTestContext {

    @Test
    public void shouldRegisterMotechListenersOnAppStartUp() {
        
        AppContextListener contextListener = new AppContextListener();
        contextListener.contextInitialized(new ServletContextEvent(servletContext));

        Set<EventListener> eventListeners = Context.getInstance().getEventListenerRegistry().getListeners(MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT);
        MotechListenerEventProxy pregnancyListener = (MotechListenerEventProxy) eventListeners.iterator().next();
        assertEquals(ProgramMessageEventHandler.class.getCanonicalName(), pregnancyListener.getIdentifier());
    }
}
