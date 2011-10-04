package org.motechproject.ghana.mtn.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSubscriptions extends MotechAuditableRepository<Subscription> {
    @Autowired
    protected AllSubscriptions(@Qualifier("dbConnector") CouchDbConnector db) {
        super(Subscription.class, db);
    }

    @View(name = "get_all_active_subscriptions_for_subscriber", map = "function(doc) { if(doc.status === 'ACTIVE') { emit(doc.subscriber.number, doc) } }")
    public List<Subscription> getAllActiveSubscriptionsForSubscriber(String subscriberNumber) {
        ViewQuery viewQuery = createQuery("get_all_active_subscriptions_for_subscriber").key(subscriberNumber).includeDocs(true);
        return db.queryView(viewQuery, Subscription.class);
    }

    @View(name = "find_by_mobile_number_and_program_name", map = "function(doc) { if(doc.status === 'ACTIVE') { emit([doc.subscriber.number, doc.programType.programName], null) } }")
    public Subscription findBy(String subscriberNumber, String programName) {
        List<Subscription> subscriptions = queryView("find_by_mobile_number_and_program_name", ComplexKey.of(subscriberNumber, programName));
        return subscriptions.size() > 0 ? subscriptions.get(0) : null;
    }
}