package org.motechproject.ghana.mtn.billing.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'MTNMockUser'")
public class MTNMockUser extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "MTNMockUser";
    private String mobileNumber;
    private Money balance;

    public MTNMockUser() {
    }

    public MTNMockUser(String mobileNumber, Money balance) {
        this.mobileNumber = mobileNumber;
        this.balance = balance;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }
}
