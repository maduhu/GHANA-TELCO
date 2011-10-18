package org.motechproject.ghana.mtn.billing.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.billing.domain.MTNMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllMTNMockUsers extends MotechAuditableRepository<MTNMockUser> {
    @Autowired
    protected AllMTNMockUsers(@Qualifier("billingDbConnector") CouchDbConnector db) {
        super(MTNMockUser.class, db);
    }

    @GenerateView
    public List<MTNMockUser> findByMobileNumber(String mobileNumber) {
        return queryView("by_mobileNumber", mobileNumber);
    }
}
