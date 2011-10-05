package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.ProgramMessageAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllProgramMessageAudits extends MotechAuditableRepository<ProgramMessageAudit> {

    @Autowired
    protected AllProgramMessageAudits(@Qualifier("dbConnector") CouchDbConnector db) {
        super(ProgramMessageAudit.class, db);
    }

}
