package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.SubscriptionMessage;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSubscriptionMessages extends MotechAuditableRepository<SubscriptionMessage> {

    @Autowired
    protected AllSubscriptionMessages(@Qualifier("dbConnector") CouchDbConnector db) {
        super(SubscriptionMessage.class, db);
    }

    public SubscriptionMessage findBy(ProgramType type, Week week, Day day) {
        List<SubscriptionMessage> messages = findByProgramName(type.getProgramName());
        for (SubscriptionMessage message : messages)
            if (message.isOf(week, day)) return message;
        return null;
    }

    @GenerateView
    public List<SubscriptionMessage> findByProgramName(String programName) {
        return queryView("by_programName", programName);
    }

}
