package org.motechproject.ghana.mtn.billing.repository;

import org.junit.After;
import org.junit.runner.RunWith;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.model.MotechAuditableDataObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContextBilling.xml"})
public abstract class RepositoryTest<T extends MotechAuditableDataObject> {

    private MotechAuditableRepository<T> repository;

    public void setRepository(MotechAuditableRepository<T> repository) {
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
