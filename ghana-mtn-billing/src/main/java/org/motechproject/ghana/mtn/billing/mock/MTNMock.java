package org.motechproject.ghana.mtn.billing.mock;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.vo.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class MTNMock {
    private static final Logger log = Logger.getLogger(MTNMock.class);
    private BufferedReader fileForSubscribers;
    private String configFile;

    @Autowired
    public MTNMock(@Value(value = "#{mockMTNProperties['configFile']}") String configFile) throws IOException {
        this.configFile = configFile;
    }

    public Double getBalanceFor(String mobileNumber) {
        try {
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
        } catch (IOException e) {
            log.error(e);
        }
        return 0D;
    }

    public boolean isMtnCustomer(String mobileNumber) {
        try {
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
        } catch (IOException e) {
            log.error(e);
        }
        return false;
    }

    public Money chargeCustomer(String mobileNumber, double amountToCharge) {
        return new Money(amountToCharge);
    }
}
