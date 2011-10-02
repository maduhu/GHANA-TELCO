package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllSubscribers extends MotechAuditableRepository<Subscriber> {
    @Autowired
    protected AllSubscribers(@Qualifier("dbConnector") CouchDbConnector db) {
        super(Subscriber.class, db);
    }
}
