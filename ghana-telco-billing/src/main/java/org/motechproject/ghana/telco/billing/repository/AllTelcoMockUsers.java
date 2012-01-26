package org.motechproject.ghana.telco.billing.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.telco.billing.domain.TelcoMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllTelcoMockUsers extends MotechBaseRepository<TelcoMockUser> {
    @Autowired
    protected AllTelcoMockUsers(@Qualifier("billingDbConnector") CouchDbConnector db) {
        super(TelcoMockUser.class, db);
    }

    @GenerateView
    public List<TelcoMockUser> findByMobileNumber(String mobileNumber) {
        return queryView("by_mobileNumber", mobileNumber);
    }
}
