package org.motechproject.ghana.mtn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.mtn.domain.ProgramType;
import org.motechproject.ghana.mtn.domain.SMSAudit;
import org.motechproject.ghana.mtn.domain.Subscription;
import org.motechproject.ghana.mtn.repository.AllSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = "classpath:test_applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SMSAuditAnalyzerTest {
    @Autowired
    private SMSAuditAnalyzer smsAuditAnalyzer;
    @Autowired
    private AllSubscriptions allSubscriptions;

    @Before
    public void setUp() {

    }

    @Test
    public void shouldGetSmsAuditsForASubscriber() {
        List<Subscription> allActiveSubscriptions = new ArrayList<Subscription>();
        allActiveSubscriptions.addAll(allSubscriptions.getAllActiveSubscriptions(ProgramType.CHILDCARE));
        allActiveSubscriptions.addAll(allSubscriptions.getAllActiveSubscriptions(ProgramType.PREGNANCY));

        //assertThat(allActiveSubscriptions.size(), is(equalTo(9959))); // TODO: Update to actual number from couchdb

        List<SMSAudit> smsAuditForDate = smsAuditAnalyzer.getSmsAuditForDate("2011-12-19");
        diff(allActiveSubscriptions,smsAuditForDate);
       // assertThat(9943, is(equalTo(smsAuditForDate.size())));

    }

    public void diff(List<Subscription> allSubscriptions, List<SMSAudit> smsAuditForDate) {
        List<String> sentNumbers = new ArrayList<String>();
        List<String> allNumbers = new ArrayList<String>();
        for (SMSAudit sa : smsAuditForDate) {
            sentNumbers.add(sa.getSubscriberNumber());
        }

        for (Subscription s : allSubscriptions) {
            allNumbers.add(s.subscriberNumber());
        }

        writeToFile(allNumbers, "/home/sanjana/allNumbers.txt");
        allNumbers.removeAll(sentNumbers);
        writeToFile(sentNumbers, "/home/sanjana/sentNumbers.txt");
        writeToFile(allNumbers, "/home/sanjana/failedNumbers.txt");


    }

    private void writeToFile(List<String> allNumbersBackUp, String fileName) {
        try {
            FileWriter fstream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fstream);
            for (String num : allNumbersBackUp) {
                out.write(num);
                out.write("\n");
            }
            out.close();
        } catch (IOException e) {

        }
    }

}
