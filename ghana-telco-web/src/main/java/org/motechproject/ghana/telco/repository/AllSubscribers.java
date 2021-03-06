package org.motechproject.ghana.telco.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.telco.domain.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllSubscribers extends MotechBaseRepository<Subscriber> {
    @Autowired
    protected AllSubscribers(@Qualifier("dbConnector") CouchDbConnector db) {
        super(Subscriber.class, db);
    }
}
