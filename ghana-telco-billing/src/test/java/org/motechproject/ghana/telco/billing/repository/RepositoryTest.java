package org.motechproject.ghana.telco.billing.repository;

import org.junit.After;
import org.junit.runner.RunWith;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.model.MotechBaseDataObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContextBilling.xml"})
public abstract class RepositoryTest<T extends MotechBaseDataObject> {

    private MotechBaseRepository<T> repository;

    public void setRepository(MotechBaseRepository<T> repository) {
        this.repository = repository;
    }

    @After
    public void tearDown() {
         removeAll();
    }

    public void removeAll() {
        for (T t : repository.getAll()) {
            repository.remove(t);
        }
    }
}
