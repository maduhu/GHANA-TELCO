package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isEmpty;

@Repository
public class AllMessages extends MotechAuditableRepository<Message> {

    @Autowired
    protected AllMessages(@Qualifier("dbConnector") CouchDbConnector db) {
        super(Message.class, db);
    }

    public Message findBy(String key) {
        List<Message> messages = findByKey(key);
        return isNotEmpty(messages) ? messages.get(0) : null;
    }

    @GenerateView
    public List<Message> findByKey(String queryKey) {
        return isEmpty(queryKey) ? null : queryView("by_key", queryKey);
    }
}
