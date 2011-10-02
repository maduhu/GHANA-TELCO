package org.motechproject.ghana.mtn.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.domain.SubscriptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSubscriptionTypes extends MotechAuditableRepository<SubscriptionType> {
    @Autowired
    protected AllSubscriptionTypes(@Qualifier("ghanaMtnDBConnector") CouchDbConnector db) {
        super(SubscriptionType.class, db);
    }

    public SubscriptionType findByCampaignShortCode(String shortCode) {
        List<SubscriptionType> subscriptionTypes = getAll();
        for (SubscriptionType subscriptionType : subscriptionTypes) {
            if (subscriptionType.getShortCodes().contains(shortCode))
                return subscriptionType;
        }
        return null;
    }

}
