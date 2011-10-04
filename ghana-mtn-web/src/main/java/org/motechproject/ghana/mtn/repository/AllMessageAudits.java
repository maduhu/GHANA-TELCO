package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.MessageAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllMessageAudits extends MotechAuditableRepository<MessageAudit> {

    @Autowired
    protected AllMessageAudits(@Qualifier("dbConnector") CouchDbConnector db) {
        super(MessageAudit.class, db);
    }
}
