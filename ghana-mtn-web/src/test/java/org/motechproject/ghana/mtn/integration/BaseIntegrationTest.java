package org.motechproject.ghana.mtn.integration;

import org.motechproject.ghana.mtn.BaseSpringTestContext;
import org.motechproject.ghana.mtn.billing.domain.MTNMockUser;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.billing.repository.AllMTNMockUsers;
import org.motechproject.ghana.mtn.controller.SubscriptionController;
import org.motechproject.ghana.mtn.domain.*;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.domain.builder.ShortCodeBuilder;
import org.motechproject.ghana.mtn.domain.dto.SubscriptionRequest;
import org.motechproject.ghana.mtn.matchers.ProgramTypeMatcher;
import org.motechproject.ghana.mtn.repository.*;
import org.motechproject.ghana.mtn.vo.Money;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public abstract class BaseIntegrationTest extends BaseSpringTestContext {

    @Autowired
    protected SubscriptionController subscriptionController;
    @Autowired
    protected AllSubscriptions allSubscriptions;
    @Autowired
    protected AllSubscribers allSubscribers;
    @Autowired
    protected AllProgramTypes allProgramTypes;
    @Autowired
    protected AllBillAccounts allBillAccounts;
    @Autowired
    protected AllShortCodes allShortCodes;
    @Autowired
    AllMTNMockUsers allMtnMock;
    @Autowired
    protected AllSMSAudits allSMSAudits;

    protected ShortCode shortCode = new ShortCodeBuilder().withCodeKey(ShortCode.RELATIVE).withShortCode("R").build();
    public final ProgramType childCarePregnancyType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(1).withMaxWeek(52).withProgramKey(IProgramType.CHILDCARE).withProgramName("Child Care").withShortCode("C").withShortCode("c").build();
    public final ProgramType pregnancyProgramType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(5).withMaxWeek(35).withProgramKey(IProgramType.PREGNANCY).withProgramName("Pregnancy").withShortCode("P").withShortCode("p").build();
    protected MTNMockUser mtnMockUser = new MTNMockUser("9500012345", new Money(10D));

    protected SubscriptionRequest createSubscriptionRequest(String inputMessage, String subscriberNumber) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setInputMessage(inputMessage);
        subscriptionRequest.setSubscriberNumber(subscriberNumber);
        return subscriptionRequest;
    }

    protected void enroll(String subscriberNumber, String inputMessage) {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest(inputMessage, subscriberNumber);
        subscriptionController.handle(subscriptionRequest);

        assertEquals(1, allSubscriptions.getAllActiveSubscriptionsForSubscriber(subscriberNumber));
    }

    protected void assertEnrollmentDetails(Subscription subscription) {
        ProgramType programType = allProgramTypes.findByCampaignShortCode(subscription.getProgramType().getShortCodes().get(0));
        assertThat(programType, new ProgramTypeMatcher(subscription.getProgramType()));
        assertThat(subscription.getStatus(), is(SubscriptionStatus.ACTIVE));
        assertTrue(subscription.getStartWeekAndDay().getWeek().getNumber() >= programType.getMinWeek());
        assertTrue(subscription.getStartWeekAndDay().getWeek().getNumber() <= programType.getMaxWeek());
        assertNotNull(subscription.getSubscriber());

        assertCampaignSchedule(subscription);
        assertBillingSchedule(subscription);
    }

    private void assertBillingSchedule(Subscription subscription) {
    }

    protected void assertCampaignSchedule(Subscription subscription) {

    }

    protected void assertMessageSentToUser(String message) {
        List<SMSAudit> smsAudits = allSMSAudits.getAll();
        assertThat(smsAudits.get(smsAudits.size() - 1).getContent(), is(message));
    }
}
