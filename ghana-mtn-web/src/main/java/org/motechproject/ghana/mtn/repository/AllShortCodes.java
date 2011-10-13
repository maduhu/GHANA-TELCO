package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.ShortCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllShortCodes extends MotechAuditableRepository<ShortCode> {
    @Autowired
    protected AllShortCodes(@Qualifier("dbConnector") CouchDbConnector db) {
        super(ShortCode.class, db);
    }

    @View(name = "get_all_short_code_for", map = "function(doc) { emit(doc.codeKey, doc) }")
    public List<ShortCode> getAllCodesFor(String codeKey) {
        ViewQuery query = createQuery("get_all_short_code_for").key(codeKey);
        return db.queryView(query, ShortCode.class);
    }
}
