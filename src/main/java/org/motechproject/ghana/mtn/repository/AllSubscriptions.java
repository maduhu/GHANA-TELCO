package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AllSubscriptions extends MotechAuditableRepository<Subscription> {
    @Autowired
    protected AllSubscriptions(@Qualifier("ghanaMtnDBConnector") CouchDbConnector db) {
        super(Subscription.class, db);
    }
}
