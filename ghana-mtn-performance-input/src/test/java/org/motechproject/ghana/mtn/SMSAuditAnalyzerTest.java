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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@ContextConfiguration(locations = "classpath:test_applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SMSAuditAnalyzerTest {
    @Autowired
    private SMSAuditAnalyzer smsAuditAnalyzer;
    @Autowired
    private AllSubscriptions allSubscriptions;

    @Before
    public void setUp() {
        smsAuditAnalyzer = new SMSAuditAnalyzer();
    }

    @Test
    public void shouldGetSmsAuditsForASubscriber() {
        List<Subscription> allActiveSubscriptions = new ArrayList<Subscription>();
        allActiveSubscriptions.addAll(allSubscriptions.getAllActiveSubscriptions(ProgramType.CHILDCARE));
        allActiveSubscriptions.addAll(allSubscriptions.getAllActiveSubscriptions(ProgramType.PREGNANCY));

        assertThat(allActiveSubscriptions.size(), is(equalTo(9478))); // TODO: Update to actual number from couchdb

        List<SMSAudit> smsAuditForDate = smsAuditAnalyzer.getSmsAuditForDate("2011-12-09");
        assertThat(9943, is(equalTo(smsAuditForDate.size())));

        // TODO: Take all the subscriberNumbers from smsAudits and compare with the subscribers from allActiveSubscriptions.
        // See if the missedout subscribers are of all same program type.  etc
        // basically try to do some analysis on this data.

    }
}
