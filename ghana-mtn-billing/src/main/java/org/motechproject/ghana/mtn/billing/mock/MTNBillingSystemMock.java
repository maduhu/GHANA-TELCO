package org.motechproject.ghana.mtn.billing.mock;

import org.motechproject.ghana.mtn.dto.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class MTNBillingSystemMock {
    private BufferedReader fileForSubscribers;
    private String configFile;

    @Autowired
    public MTNBillingSystemMock(@Value(value = "#{mockMTNProperties['configFile']}") String configFile) throws IOException {
        this.configFile = configFile;
    }

    public Double getAvailableBalance(String mobileNumber) throws IOException {
        fileForSubscribers = new BufferedReader(new FileReader(this.configFile));
        String subscriber;
        while (null != (subscriber = fileForSubscribers.readLine())) {
            String[] subscriberDetails = subscriber.split(",");
            if (subscriberDetails[0].equals(mobileNumber)) {
                fileForSubscribers.close();
                return Double.valueOf(subscriberDetails[1]);
            }
        }
        fileForSubscribers.close();
        return 0D;
    }

    public boolean isMtnCustomer(String mobileNumber) throws IOException {
        fileForSubscribers = new BufferedReader(new FileReader(this.configFile));
        String subscriber;
        while (null != (subscriber = fileForSubscribers.readLine())) {
            String[] subscriberDetails = subscriber.split(",");
            if (subscriberDetails[0].equals(mobileNumber)) {
                fileForSubscribers.close();
                return true;
            }
        }

        fileForSubscribers.close();
        return false;
    }

    public void chargeCustomer(String mobileNumber, double amountToCharge) {
    }
}
