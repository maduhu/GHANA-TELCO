package org.motechproject.ghana.mtn.billing.mock;

import org.apache.commons.collections.set.UnmodifiableSortedSet;
import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.billing.domain.MTNMockUser;
import org.motechproject.ghana.mtn.billing.repository.AllMTNMockUsers;
import org.motechproject.ghana.mtn.vo.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Component
public class MTNMock {
    private String configFile;
    private AllMTNMockUsers allMTNMockUsers;

    @Autowired
    public MTNMock(@Value(value = "#{mockMTNProperties['configFile']}") String configFile, AllMTNMockUsers allMTNMockUsers) throws IOException {
        this.configFile = configFile;
        this.allMTNMockUsers = allMTNMockUsers;
    }

    public Double getBalanceFor(String mobileNumber) {
        MTNMockUser user = fetchUser(mobileNumber);
        if (user != null)
            return user.getBalance().getValue();
        return 0d;
    }

    public boolean isMtnCustomer(String mobileNumber) {
        MTNMockUser user = fetchUser(mobileNumber);
        return user != null;
    }

    public Money chargeCustomer(String mobileNumber, double amountToCharge) {
        MTNMockUser user = fetchUser(mobileNumber);
        if (user != null) user.getBalance().subtract(amountToCharge);
        return new Money(amountToCharge);
    }

    private MTNMockUser fetchUser(String mobileNumber) {
        List<MTNMockUser> users = allMTNMockUsers.findByMobileNumber(mobileNumber);
        return users != null & !users.isEmpty() ? users.get(0) : null;
    }
}
