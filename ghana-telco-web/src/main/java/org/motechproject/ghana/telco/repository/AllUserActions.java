package org.motechproject.ghana.telco.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.telco.domain.UserAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllUserActions extends MotechBaseRepository<UserAction> {
    @Autowired
    public AllUserActions(@Qualifier("dbConnector") CouchDbConnector db) {
        super(UserAction.class, db);
    }
}
