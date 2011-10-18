package org.motechproject.ghana.mtn.tools.seed;

import org.motechproject.ghana.mtn.billing.domain.MTNMockUser;
import org.motechproject.ghana.mtn.billing.repository.AllMTNMockUsers;
import org.motechproject.ghana.mtn.vo.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockMTNUserSeed extends Seed {

    @Autowired
    private AllMTNMockUsers allMTNMockUsers;

    @Override
    protected void load() {
        allMTNMockUsers.add(new MTNMockUser("9500012345", new Money(10d)));
        allMTNMockUsers.add(new MTNMockUser("9876543210", new Money(20d)));
        allMTNMockUsers.add(new MTNMockUser("1234567890", new Money(10d)));
        allMTNMockUsers.add(new MTNMockUser("9986574410", new Money(0d)));
        allMTNMockUsers.add(new MTNMockUser("9686601234", new Money(-34d)));
        allMTNMockUsers.add(new MTNMockUser("9855123459", new Money(0.6d)));
        allMTNMockUsers.add(new MTNMockUser("7777777777", new Money(0.55d)));
        allMTNMockUsers.add(new MTNMockUser("8888888888", new Money(0.65d)));
        allMTNMockUsers.add(new MTNMockUser("0987654321", new Money(0.65d)));
    }
}
