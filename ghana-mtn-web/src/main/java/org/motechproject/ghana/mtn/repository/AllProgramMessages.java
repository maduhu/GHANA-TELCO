package org.motechproject.ghana.mtn.repository;

import org.apache.commons.collections.CollectionUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.model.DayOfWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllProgramMessages extends MotechAuditableRepository<ProgramMessage> {

    @Autowired
    protected AllProgramMessages(@Qualifier("dbConnector") CouchDbConnector db) {
        super(ProgramMessage.class, db);
    }

    public ProgramMessage findBy(ProgramType type, Week week, DayOfWeek day) {
        List<ProgramMessage> messages = findByProgramKey(type.getProgramKey());
        for (ProgramMessage message : messages)
            if (message.isOf(week, day)) return message;
        return null;
    }

    public ProgramMessage findBy(String messageKey) {
        List<ProgramMessage> messages = findByMessageKey(messageKey);
        return CollectionUtils.isEmpty(messages) ? null : messages.get(0);
    }

    @GenerateView
    public List<ProgramMessage> findByMessageKey(String messageKey) {
        return queryView("by_messageKey", messageKey);
    }

    @GenerateView
    public List<ProgramMessage> findByProgramKey(String value) {
        return queryView("by_programKey", value);
    }

}
