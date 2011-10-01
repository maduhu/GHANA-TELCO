package org.motechproject.ghana.mtn.listener;

import org.junit.Test;
import org.motechproject.context.Context;
import org.motechproject.ghana.mtn.SpringTestContext;
import org.motechproject.server.event.EventListener;
import org.motechproject.server.event.annotations.MotechListenerEventProxy;

import javax.servlet.ServletContextEvent;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;


public class AppContextListenerTest extends SpringTestContext {

    @Test
    public void shouldRegisterMotechListenersOnAppStartUp() {
        
        AppContextListener contextListener = new AppContextListener();
        contextListener.contextInitialized(new ServletContextEvent(servletContext));

        Set<EventListener> eventListeners = Context.getInstance().getEventListenerRegistry().getListeners(MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT);
        MotechListenerEventProxy pregnancyListener = (MotechListenerEventProxy) eventListeners.iterator().next();
        assertEquals(PregnancyMessageListener.class.getCanonicalName(), pregnancyListener.getIdentifier());
    }
}
