package org.motechproject.ghana.mtn.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
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

    @View(name = "find_by_mobile_number_and_program_key", map = "function(doc) { if(doc.status === 'ACTIVE') { emit([doc.subscriber.number, doc.programType.programKey], null) } }")
    public Subscription findActiveSubscriptionFor(String subscriberNumber, String programKey) {
        List<Subscription> subscriptions = queryView("find_by_mobile_number_and_program_key", ComplexKey.of(subscriberNumber, programKey));
        return subscriptions.size() > 0 ? subscriptions.get(0) : null;
    }

    @View(name = "find_by_mobile_number_and_program_key_and_status", map = "function(doc) { emit([doc.subscriber.number, doc.programType.programKey, doc.status], null) }")
    public Subscription findBy(String subscriberNumber, String programKey, SubscriptionStatus subscriptionStatus) {
        List<Subscription> subscriptions = queryView("find_by_mobile_number_and_program_key_and_status", ComplexKey.of(subscriberNumber, programKey, subscriptionStatus));
        return subscriptions.size() > 0 ? subscriptions.get(0) : null;
    }

    @View(name = "find_all_Active_Subscriptions", map = "function(doc) {" +
            "if (doc.status === 'ACTIVE' || doc.status === 'SUSPENDED' || doc.status === 'WAITING_FOR_ROLLOVER_RESPONSE')" +
            "  emit( doc.programType.programKey, [doc.registrationDate, doc.subscriber.number] );  }")
    public List<Subscription> getAllActiveSubscriptions(String programKey) {
        ViewQuery viewQuery = createQuery("find_all_Active_Subscriptions").key(programKey).includeDocs(true);
        return db.queryView(viewQuery, Subscription.class);
    }
}
