package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllAppConfigs extends MotechAuditableRepository<AppConfig> {

    @Autowired
    protected AllAppConfigs(@Qualifier("dbConnector") CouchDbConnector db) {
        super(AppConfig.class, db);
    }
}
