package org.motechproject.ghana.mtn.billing.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AllBillAudits extends MotechAuditableRepository<BillAudit> {

    @Autowired
    protected AllBillAudits(@Qualifier("billingDbConnector") CouchDbConnector db) {
        super(BillAudit.class, db);
    }
}
