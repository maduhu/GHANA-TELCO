package org.motechproject.ghana.telco.billing.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.telco.billing.domain.BillAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllBillAudits extends MotechAuditableRepository<BillAudit> {

    public static final String FETCH_ALL_BILL_AUDITS_FOR = "fetch_all_bill_audits_for_subscriber_and_program";

    @Autowired
    protected AllBillAudits(@Qualifier("billingDbConnector") CouchDbConnector db) {
        super(BillAudit.class, db);
    }

    @View(name = FETCH_ALL_BILL_AUDITS_FOR, map = "function(doc) { if (doc.type === 'BillAudit') emit([doc.mobileNumber, doc.program], doc) }")
    public List<BillAudit> fetchAuditsFor(String subscriberNumber, String programKey) {
         return queryView(FETCH_ALL_BILL_AUDITS_FOR, ComplexKey.of(subscriberNumber, programKey));
    }
}
