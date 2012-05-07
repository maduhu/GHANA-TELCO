package org.motechproject.ghana.telco.repository;

import org.apache.commons.collections.CollectionUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.telco.domain.ProgramMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllProgramMessages extends MotechBaseRepository<ProgramMessage> {

    @Autowired
    protected AllProgramMessages(@Qualifier("dbConnector") CouchDbConnector db) {
        super(ProgramMessage.class, db);
    }

    public ProgramMessage findBy(String messageKey) {
        List<ProgramMessage> messages = findByMessageKey(messageKey);
        return CollectionUtils.isEmpty(messages) ? null : messages.get(0);
    }

    @GenerateView
    public List<ProgramMessage> findByMessageKey(String messageKey) {
        return queryView("by_messageKey", messageKey);
    }
}
