package org.motechproject.ghana.telco.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.telco.domain.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
public class AllAppConfigs extends MotechAuditableRepository<AppConfig> {

    @Autowired
    protected AllAppConfigs(@Qualifier("dbConnector") CouchDbConnector db) {
        super(AppConfig.class, db);
    }

    @GenerateView
    public AppConfig findByKey(String key) {
        List<AppConfig> configs = queryView("by_key", key);
        return CollectionUtils.isEmpty(configs) ? null : configs.get(0);
    }
}
