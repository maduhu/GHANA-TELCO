package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.service.process.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
        List<BaseSubscriptionProcess> processes = Arrays.asList(validation, billing, persistence, campaign);
        for (BaseSubscriptionProcess process : processes) {
            if (process.startFor(subscription)) continue;
            break;
        }
    }

    @Override
    public void stop(Subscription subscription) {
        List<BaseSubscriptionProcess> processes = Arrays.asList(billing, campaign, persistence);
        for (BaseSubscriptionProcess process : processes) {
            if (process.endFor(subscription)) continue;
            break;
        }
    }

}
