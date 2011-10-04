package org.motechproject.ghana.mtn.billing.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;

@TypeDiscriminator("doc.type === 'BillAccount'")
public class BillAccount extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "BillAccount";
    private String mobileNumber;
    private Double currentBalance;
    private List<BillProgramAccount> programAccounts;

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public void setProgramFee(String programName, Double fee) {
        if (null == programAccounts)
            programAccounts = new ArrayList<BillProgramAccount>();

        BillProgramAccount programAccount = selectFirst(programAccounts, having(on(BillProgramAccount.class).getProgramName(), org.hamcrest.Matchers.equalTo(programName))) ;

        if (programAccount == null) {
            programAccount = new BillProgramAccount(programName, fee);
            programAccounts.add(programAccount);
        }

        programAccount.setFee(fee);
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public List<BillProgramAccount> getProgramAccounts() {
        return programAccounts;
    }

    public void setProgramAccounts(List<BillProgramAccount> programAccounts) {
        this.programAccounts = programAccounts;
    }
}
