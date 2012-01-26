package org.motechproject.ghana.telco.billing.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.telco.vo.Money;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'TelcoMockUser'")
public class TelcoMockUser extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "TelcoMockUser";
    private String mobileNumber;
    private Money balance;

    public TelcoMockUser() {
    }

    public TelcoMockUser(String mobileNumber, Money balance) {
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
