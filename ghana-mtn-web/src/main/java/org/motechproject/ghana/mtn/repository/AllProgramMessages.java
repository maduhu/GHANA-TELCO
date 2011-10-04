package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.ProgramMessage;
import org.motechproject.ghana.mtn.domain.vo.Day;
import org.motechproject.ghana.mtn.domain.vo.Week;
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

    public ProgramMessage findBy(ProgramType type, Week week, Day day) {
        List<ProgramMessage> messages = findByProgramName(type.getProgramName());
        for (ProgramMessage message : messages)
            if (message.isOf(week, day)) return message;
        return null;
    }

    @GenerateView
    public List<ProgramMessage> findByProgramName(String programName) {
        return queryView("by_programName", programName);
    }

}
