package org.motechproject.ghana.telco.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.telco.domain.ShortCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllShortCodes extends MotechBaseRepository<ShortCode> {
    @Autowired
    protected AllShortCodes(@Qualifier("dbConnector") CouchDbConnector db) {
        super(ShortCode.class, db);
    }

    @View(name = "get_all_short_code_for", map = "function(doc) { emit(doc.codeKey, doc) }")
    public ShortCode getShortCodeFor(String codeKey) {
        ViewQuery query = createQuery("get_all_short_code_for").key(codeKey);
        List<ShortCode> shortCodes = db.queryView(query, ShortCode.class);
        return null != shortCodes ? shortCodes.get(0) : null;
    }
}
