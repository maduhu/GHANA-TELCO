package org.motechproject.ghana.telco.billing.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.telco.vo.Money;
import org.motechproject.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;

@TypeDiscriminator("doc.type === 'BillAccount'")
public class BillAccount extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "BillAccount";
    private String mobileNumber;
    private Money currentBalance;
    private List<BillProgramAccount> programAccounts;

    @JsonIgnore
    public void setProgramFee(String programName, Money fee) {
        if (null == programAccounts)
            programAccounts = new ArrayList<BillProgramAccount>();

        BillProgramAccount programAccount = selectFirst(programAccounts, having(on(BillProgramAccount.class).getProgramKey(), org.hamcrest.Matchers.equalTo(programName))) ;

        if (programAccount == null) {
            programAccount = new BillProgramAccount(programName, fee);
            programAccounts.add(programAccount);
        }

        programAccount.setFee(fee);
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setCurrentBalance(Money currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Money getCurrentBalance() {
        return currentBalance;
    }

    public List<BillProgramAccount> getProgramAccounts() {
        return programAccounts;
    }

    public void setProgramAccounts(List<BillProgramAccount> programAccounts) {
        this.programAccounts = programAccounts;
    }
}
