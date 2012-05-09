package org.motechproject.ghana.telco.repository;

import org.apache.commons.collections.CollectionUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.telco.domain.TelcoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.ektorp.ComplexKey.of;

@Repository
public class AllTelcoUsers extends MotechBaseRepository<TelcoUser> {

    @Autowired
    protected AllTelcoUsers(@Qualifier("dbConnector") CouchDbConnector db) {
        super(TelcoUser.class, db);
    }

    @View(name = "by_encrypted_username_and_password", map = "function(doc) { emit([doc.userName, doc.password], doc) }")
    public TelcoUser findBy(String userName, String password) {
        ViewQuery viewQuery = createQuery("by_encrypted_username_and_password").key(of(userName, password)).includeDocs(true);
        final List<TelcoUser> telcoUsers = db.queryView(viewQuery, TelcoUser.class);
        return CollectionUtils.isEmpty(telcoUsers) ? null : telcoUsers.get(0);
    }
}
