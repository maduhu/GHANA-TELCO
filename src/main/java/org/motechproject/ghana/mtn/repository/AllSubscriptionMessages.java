package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.SubscriptionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllSubscriptionMessages extends MotechAuditableRepository<SubscriptionMessage> {
    @Autowired
    protected AllSubscriptionMessages(@Qualifier("dbConnector") CouchDbConnector db) {
        super(SubscriptionMessage.class, db);
    }
}
