package org.motechproject.ghana.mtn;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.GenericWebApplicationContext;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class SpringTestContext extends AbstractJUnit4SpringContextTests {

    protected MockHttpServletResponse response;
    protected MockHttpServletRequest request;
    protected MockHttpSession session;
    protected MockServletContext servletContext;

    @Before
    public final void init() {
        initMocks(this);

        DefaultListableBeanFactory dlbf = new DefaultListableBeanFactory(this.applicationContext.getAutowireCapableBeanFactory());
        GenericWebApplicationContext appContext = new GenericWebApplicationContext(dlbf);
        servletContext = new MockServletContext();
        servletContext.setAttribute(GenericWebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appContext);
        appContext.setServletContext(servletContext);
        appContext.refresh();

        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest(servletContext);

        session = new MockHttpSession(servletContext);
        request.setSession(session);
    }
}

