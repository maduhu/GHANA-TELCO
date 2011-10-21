package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.process.*;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private AllSubscriptions allSubscriptions;
    private ValidationProcess validation;
    private BillingCycleProcess billing;
    private PersistenceProcess persistence;
    private CampaignProcess campaign;

    @Autowired
    public SubscriptionServiceImpl(AllSubscriptions allSubscriptions,
                                   ValidationProcess validation,
                                   BillingCycleProcess billing,
                                   PersistenceProcess persistence,
                                   CampaignProcess campaign) {
        this.allSubscriptions = allSubscriptions;
        this.validation = validation;
        this.billing = billing;
        this.persistence = persistence;
        this.campaign = campaign;
    }

    @Override
    public void start(Subscription subscription) {
        for (ISubscriptionFlowProcess process : asList(validation, billing, persistence, campaign)) {
            if (process.startFor(subscription)) continue;
            break;
        }
    }

    @Override
    public void stopExpired(Subscription subscription) {
        for (ISubscriptionFlowProcess process : asList(billing, campaign, persistence)) {
            if (process.stopExpired(subscription)) continue;
            break;
        }
    }

    @Override
    public void stopByUser(String subscriberNumber, IProgramType programType) {
        Subscription subscription = validation.validateSubscriptionToStop(subscriberNumber, programType);
        if (subscription != null) {
            for (ISubscriptionFlowProcess process : asList(billing, campaign, persistence)) {
                if (process.stopByUser(subscription)) continue;
                break;
            }
        }
    }

    @Override
    public void rollOver(Subscription source, Subscription target) {
        for (ISubscriptionFlowProcess process : asList(validation, billing, campaign, persistence)) {
            if (process.rollOver(source, target)) continue;
            break;
        }
    }

    @Override
    public void processAfterEvent(Subscription subscription) {
        if (!subscription.isCompleted()) return;
        if (!subscription.canRollOff()) stopExpired(subscription);
        Subscription nextSubscription = new Subscription(
                subscription.getSubscriber(),
                subscription.getProgramType().getRollOverProgramType(),
                SubscriptionStatus.ACTIVE,
                new WeekAndDay(subscription.currentWeek(), subscription.currentDay()),
                DateUtil.now());
        rollOver(subscription, nextSubscription);
    }

    @Override
    public Subscription findBy(String subscriberNumber, String programName) {
        return allSubscriptions.findBy(subscriberNumber, programName);
    }

    @Override
    public List<Subscription> activeSubscriptions(String subscriberNumber) {
        return allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber);
    }

}
