package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.process.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private SubscriptionValidation validation;
    private SubscriptionBilling billing;
    private SubscriptionPersistence persistence;
    private SubscriptionCampaign campaign;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionValidation validation,
                                   SubscriptionBilling billing,
                                   SubscriptionPersistence persistence,
                                   SubscriptionCampaign campaign) {
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

    private List<ISubscriptionProcessFlow> processes(ISubscriptionProcessFlow... processes) {
        List<ISubscriptionProcessFlow> list = new ArrayList<ISubscriptionProcessFlow>();
        for (ISubscriptionProcessFlow process : processes) list.add(process);
        return list;
    }

}
