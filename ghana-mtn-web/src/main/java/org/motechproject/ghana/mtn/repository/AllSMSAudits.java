package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.SMSAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSMSAudits extends MotechAuditableRepository<SMSAudit> {

    @Autowired
    protected AllSMSAudits(@Qualifier("dbConnector") CouchDbConnector db) {
        super(SMSAudit.class, db);
    }

    @View(name = "fetch_sms_by_date", map = "function(doc) { if(doc.type === 'SMSAudit') emit(doc.sentTime.substring(0,10), doc) }")
    public List<SMSAudit> fetchSmsByDate(String date) {
        ViewQuery viewQuery = createQuery("fetch_sms_by_date").key(date);
        return db.queryView(viewQuery, SMSAudit.class);
    }
}
