package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.SubscriptionMessage;
import org.springframework.beans.factory.annotation.Qualifier;

public class AllSubscriptionMessages extends MotechAuditableRepository<SubscriptionMessage> {

    protected AllSubscriptionMessages(@Qualifier("ghanaMtnDBConnector") CouchDbConnector db) {
        super(SubscriptionMessage.class, db);
    }
}
