package org.motechproject.ghana.mtn.billing.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.billing.domain.BillAccount;
import org.motechproject.ghana.mtn.billing.domain.BillProgramAccount;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.dto.Money;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AllBillAccountsTest extends RepositoryTest {
    @Autowired
    private AllBillAccounts allBillingAccounts;

    @Before
    public void setUp(){
       setRepository(allBillingAccounts);
       removeAll();
    }

    @Test
    public void ShouldUpdateBillAccount() {
        String mobileNumber = "9500012345";
        Double currentBalance = 2D;
        allBillingAccounts.updateFor(mobileNumber, currentBalance, getPregnancyProgramType());
        assertBillAccount();
        allBillingAccounts.updateFor(mobileNumber, currentBalance, getPregnancyProgramType());
        assertBillAccount();
    }

    private void assertBillAccount() {
        List<BillAccount> billAccounts = allBillingAccounts.getAll();
        assertThat(billAccounts.size(), is(1));

        BillAccount billAccount = billAccounts.get(0);

        List<BillProgramAccount> programAccounts = billAccount.getProgramAccounts();
        assertThat(programAccounts.size(), is(1));
        assertThat(programAccounts.get(0).getProgramName(), is(getPregnancyProgramType().getProgramName()));
    }

    public IProgramType getPregnancyProgramType() {
        return new IProgramType() {
            @Override
            public String getProgramName() {
                return "Pregnancy";
            }

            @Override
            public List<String> getShortCodes() {
                return Arrays.asList("P");
            }

            @Override
            public Integer getMinWeek() {
                return 5;
            }

            @Override
            public Integer getMaxWeek() {
                return 35;
            }

            @Override
            public Money getFee() {
                return new Money(0.60D);
            }
        };
    }

    public IProgramType getChildCareProgramType() {
        return new IProgramType() {
            @Override
            public String getProgramName() {
                return "Child Care";
            }

            @Override
            public List<String> getShortCodes() {
                return Arrays.asList("C");
            }

            @Override
            public Integer getMinWeek() {
                return 1;
            }

            @Override
            public Integer getMaxWeek() {
                return 52;
            }

            @Override
            public Money getFee() {
                return new Money(0.60D);
            }
        };
    }
}
