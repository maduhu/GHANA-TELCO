package org.motechproject.ghana.mtn.service;

import org.motechproject.ghana.mtn.domain.ProgramMessageCycle;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.domain.SubscriptionStatus;
import org.motechproject.ghana.mtn.domain.vo.Week;
import org.motechproject.ghana.mtn.domain.vo.WeekAndDay;
import org.motechproject.ghana.mtn.process.CampaignProcess;
import org.motechproject.ghana.mtn.process.ISubscriptionFlowProcess;
import org.motechproject.ghana.mtn.process.PersistenceProcess;
import org.motechproject.ghana.mtn.process.ValidationProcess;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.utils.DateUtils;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.motechproject.ghana.mtn.domain.SubscriptionStatus.WAITING_FOR_ROLLOVER_RESPONSE;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private AllSubscriptions allSubscriptions;
    private ValidationProcess validation;
    private PersistenceProcess persistence;
    private CampaignProcess campaign;
    private ProgramMessageCycle programMessageCycle;

    @Autowired
    public SubscriptionServiceImpl(AllSubscriptions allSubscriptions,
                                   ValidationProcess validation,
                                   PersistenceProcess persistence,
                                   CampaignProcess campaign, ProgramMessageCycle programMessageCycle) {
        this.allSubscriptions = allSubscriptions;
        this.validation = validation;
        this.persistence = persistence;
        this.campaign = campaign;
        this.programMessageCycle = programMessageCycle;
    }

    @Override
    public void start(Subscription subscription) {
        subscription.updateCycleInfo(programMessageCycle);
        for (ISubscriptionFlowProcess process : asList(validation, persistence, campaign)) {
            if (process.startFor(subscription)) continue;
            break;
        }
    }

    @Override
    public void stopExpired(Subscription subscription) {
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        for (ISubscriptionFlowProcess process : asList(campaign, persistence)) {
            if (process.stopExpired(subscription)) continue;
            break;
        }
    }

    @Override
    public void stopByUser(String subscriberNumber, ProgramType programType) {
        Subscription subscription = validation.validateSubscriptionToStop(subscriberNumber, programType);

        if (subscription != null) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            for (ISubscriptionFlowProcess process : asList(campaign, persistence)) {
                if (process.stopByUser(subscription)) continue;
                break;
            }
        }
    }

    @Override
    public void rollOver(String subscriberNumber, Date deliveryDate) {
        Subscription pregnancySubscription = validation.validateForRollOver(subscriberNumber, deliveryDate);
        if (null != pregnancySubscription)
            performRollOver(pregnancySubscription);
    }

    @Override
    public void rollOverByEvent(Subscription subscription) {
        if (!subscription.canRollOff())
            stopExpired(subscription);
        else
            performRollOver(subscription);
    }

    @Override
    public void retainOrRollOver(String subscriberNumber, boolean retainExistingChildCareSubscription) {
        Subscription pregnancyProgramWaitingForRollOver = allSubscriptions.findBy(subscriberNumber, ProgramType.PREGNANCY, WAITING_FOR_ROLLOVER_RESPONSE);
        Subscription existingChildCare = allSubscriptions.findActiveSubscriptionFor(subscriberNumber, ProgramType.CHILDCARE);
        if (retainExistingChildCareSubscription) {
            for (ISubscriptionFlowProcess process : asList(validation, campaign, persistence)) {
                if (!process.retainExistingChildCare(pregnancyProgramWaitingForRollOver, existingChildCare)) break;
            }
        } else {
            for (ISubscriptionFlowProcess process : asList(validation, campaign, persistence)) {
                if (!process.rollOverToNewChildCareProgram(pregnancyProgramWaitingForRollOver, rollOverSubscriptionFrom(pregnancyProgramWaitingForRollOver), existingChildCare))
                    break;
            }
        }
    }

    @Override
    public Subscription findActiveSubscriptionFor(String subscriberNumber, String programName) {
        return allSubscriptions.findActiveSubscriptionFor(subscriberNumber, programName);
    }

    @Override
    public List<Subscription> activeSubscriptions(String subscriberNumber) {
        return allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber);
    }

    private void performRollOver(Subscription subscription) {
        Subscription rollOverSubscription = rollOverSubscriptionFrom(subscription);

        for (ISubscriptionFlowProcess process : asList(validation, campaign, persistence)) {
            if (process.rollOver(subscription, rollOverSubscription)) continue;
            break;
        }
    }

    Subscription rollOverSubscriptionFrom(Subscription subscription) {
        return subscription != null ? new Subscription(
                subscription.getSubscriber(),
                subscription.rollOverProgramType(),
                SubscriptionStatus.ACTIVE,
                new WeekAndDay(new Week(subscription.rollOverProgramType().getMinWeek()), new DateUtils().today()),
                DateUtil.now()).updateCycleInfo(programMessageCycle) : null;
    }
}
