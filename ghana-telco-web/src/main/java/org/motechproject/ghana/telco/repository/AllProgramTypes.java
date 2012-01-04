package org.motechproject.ghana.telco.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.telco.domain.ProgramType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllProgramTypes extends MotechAuditableRepository<ProgramType> {
    @Autowired
    protected AllProgramTypes(@Qualifier("dbConnector") CouchDbConnector db) {
        super(ProgramType.class, db);
    }

    public ProgramType findByCampaignShortCode(String shortCode) {
        List<ProgramType> programTypes = getAll();
        String shortCodeLowerCase = shortCode.toLowerCase();

        for (ProgramType programType : programTypes) {
            List<String> shortCodes = programType.getShortCodes();
            for (String code : shortCodes) {
                if (code.toLowerCase().contains(shortCodeLowerCase))
                    return programType;
            }
        }
        return null;
    }

}
