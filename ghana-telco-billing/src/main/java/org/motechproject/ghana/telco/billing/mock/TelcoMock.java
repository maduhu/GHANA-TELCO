package org.motechproject.ghana.telco.billing.mock;

import org.motechproject.ghana.telco.billing.domain.TelcoMockUser;
import org.motechproject.ghana.telco.billing.exception.InsufficientFundsException;
import org.motechproject.ghana.telco.billing.repository.AllTelcoMockUsers;
import org.motechproject.ghana.telco.vo.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class TelcoMock {
    private AllTelcoMockUsers allTelcoMockUsers;

    @Autowired
    public TelcoMock(AllTelcoMockUsers allTelcoMockUsers) throws IOException {
        this.allTelcoMockUsers = allTelcoMockUsers;
    }

    public Double getBalanceFor(String mobileNumber) {
        TelcoMockUser user = fetchUser(mobileNumber);
        if (user != null)
            return user.getBalance().getValue();
        return 0d;
    }

    public boolean isTelcoCustomer(String mobileNumber) {
        TelcoMockUser user = fetchUser(mobileNumber);
        return user != null;
    }

    public Money chargeCustomer(String mobileNumber, double amountToCharge) throws InsufficientFundsException {
        if (getBalanceFor(mobileNumber) < amountToCharge) throw new InsufficientFundsException();

        TelcoMockUser user = fetchUser(mobileNumber);
        if (user != null) {
            user.getBalance().subtract(amountToCharge);
            allTelcoMockUsers.update(user);
        }
        return new Money(amountToCharge);
    }

    private TelcoMockUser fetchUser(String mobileNumber) {
        List<TelcoMockUser> users = allTelcoMockUsers.findByMobileNumber(mobileNumber);
        return users != null & !users.isEmpty() ? users.get(0) : null;
    }
}
