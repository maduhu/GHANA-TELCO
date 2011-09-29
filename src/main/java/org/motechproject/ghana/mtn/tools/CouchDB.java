package org.motechproject.ghana.mtn.tools;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CouchDB {
    @Autowired
    private CouchDbInstance couchDbInstance;
    @Autowired
    @Qualifier("ghanaMtnDBConnector")
    private CouchDbConnector dbConnector;

    public void recreate() {
        String dbName = dbConnector.getDatabaseName();
        couchDbInstance.deleteDatabase(dbName);
        couchDbInstance.createDatabase(dbName);
    }
}
