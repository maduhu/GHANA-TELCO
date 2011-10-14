package org.motechproject.ghana.mtn.integration;

import org.motechproject.ghana.mtn.BaseSpringTestContext;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.controller.SubscriptionController;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.builder.ProgramTypeBuilder;
import org.motechproject.ghana.mtn.repository.AllProgramTypes;
import org.motechproject.ghana.mtn.repository.AllSubscribers;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.motechproject.ghana.mtn.vo.Money;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseIntegrationTest extends BaseSpringTestContext {

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

    public final ProgramType childCarePregnancyType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(1).withMaxWeek(52).withProgramName("Child Care").withShortCode("C").withShortCode("c").build();
    public final ProgramType pregnancyProgramType = new ProgramTypeBuilder().withFee(new Money(0.60D)).withMinWeek(5).withMaxWeek(35).withProgramName("Pregnancy").withShortCode("P").withShortCode("p").build();

}
