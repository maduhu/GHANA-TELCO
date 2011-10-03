package org.motechproject.ghana.mtn;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.scheduler.MotechSchedulerServiceImpl;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext.xml"})
public abstract class BaseIntegrationTest extends AbstractJUnit4SpringContextTests {

    protected MockHttpServletResponse response;
    protected MockHttpServletRequest request;
    protected MockHttpSession session;
    protected MockServletContext servletContext;

    @Qualifier("dbConnector")
    @Autowired
    protected CouchDbConnector dbConnector;

    @Autowired
    protected CouchDbInstance couchDbInstance;

    protected ArrayList<BulkDeleteDocument> toDelete;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Before
    public void before() {
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

        toDelete = new ArrayList<BulkDeleteDocument>();
    }

    @After
    public void after() {
        deleteAll();
        //removeAllQuatzJob();
    }

    protected void deleteAll() {
        dbConnector.executeBulk(toDelete);
        toDelete.clear();
    }

    protected void markForDeletion(Object... documents) {
        for (Object document : documents)
            markForDeletion(document);
    }

    protected void markForDeletion(Object document) {
        toDelete.add(BulkDeleteDocument.of(document));
    }

    protected void removeAllQuatzJob () {
        try {
            schedulerFactoryBean.getScheduler().deleteJob("%*%" , MotechSchedulerServiceImpl.JOB_GROUP_NAME);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    protected <T extends MotechBaseDataObject> void remove(List<T> subscriptions) {
        List<BulkDeleteDocument> bulkDelete = new ArrayList<BulkDeleteDocument>();
        for (MotechBaseDataObject object : subscriptions) {
            bulkDelete.add(new BulkDeleteDocument(object.getId(), object.getRevision()));
        }
        dbConnector.executeAllOrNothing(bulkDelete);
    }
}
