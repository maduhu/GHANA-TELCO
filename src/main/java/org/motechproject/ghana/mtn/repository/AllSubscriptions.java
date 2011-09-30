package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllSubscriptions extends MotechAuditableRepository<Subscription> {
    @Autowired
    protected AllSubscriptions(@Qualifier("ghanaMtnDBConnector") CouchDbConnector db) {
        super(Subscription.class, db);
    }

    @View(name = "get_all_active_subscriptions_for_subscriber", map = "function(doc, req) { if(doc.status === 'ACTIVE') { emit(doc.subscriber.number, doc) } }")
    public List<Subscription> getAllActiveSubscriptionsForSubscriber(String subscriberNumber) {
        ViewQuery viewQuery = createQuery("get_all_active_subscriptions_for_subscriber").key(subscriberNumber).includeDocs(true);
        return db.queryView(viewQuery, Subscription.class);
    }
}
