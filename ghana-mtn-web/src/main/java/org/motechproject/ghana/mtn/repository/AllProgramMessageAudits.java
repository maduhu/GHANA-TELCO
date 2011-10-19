package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.ProgramMessageAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllProgramMessageAudits extends MotechAuditableRepository<ProgramMessageAudit> {

    @Autowired
    protected AllProgramMessageAudits(@Qualifier("dbConnector") CouchDbConnector db) {
        super(ProgramMessageAudit.class, db);
    }

    @View(name = "get_all_audits_sorted_by_date", map = "function(doc) { emit(doc.sentTime, doc); }")
    public List<ProgramMessageAudit> sortedByDate() {
        ViewQuery viewQuery = createQuery("get_all_audits_sorted_by_date").descending(true).includeDocs(true);
        return db.queryView(viewQuery, ProgramMessageAudit.class);
    }
}
