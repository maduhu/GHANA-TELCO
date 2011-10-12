package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.process.*;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private AllSubscriptions allSubscriptions;
    private SubscriptionValidation validation;
    private SubscriptionBillingCycle billing;
    private SubscriptionPersistence persistence;
    private SubscriptionCampaign campaign;

    @Autowired
    public SubscriptionServiceImpl(AllSubscriptions allSubscriptions,
                                   SubscriptionValidation validation,
                                   SubscriptionBillingCycle billing,
                                   SubscriptionPersistence persistence,
                                   SubscriptionCampaign campaign) {
        this.allSubscriptions = allSubscriptions;
        this.validation = validation;
        this.billing = billing;
        this.persistence = persistence;
        this.campaign = campaign;
    }

    @Override
    public void start(Subscription subscription) {
        for (ISubscriptionProcessFlow process : processes(validation, billing, persistence, campaign)) {
            if (process.startFor(subscription)) continue;
            break;
        }
    }

    @Override
    public void stop(Subscription subscription) {
        for (ISubscriptionProcessFlow process : processes(billing, campaign, persistence)) {
            if (process.stopFor(subscription)) continue;
            break;
        }
    }

    @Override
    public Subscription findBy(String subscriberNumber, String programName) {
        return allSubscriptions.findBy(subscriberNumber, programName);
    }

    private List<ISubscriptionProcessFlow> processes(ISubscriptionProcessFlow... processes) {
        List<ISubscriptionProcessFlow> list = new ArrayList();
        for (ISubscriptionProcessFlow process : processes)
            list.add(process);
        return list;
    }

}
