package org.motechproject.ghana.telco.repository;


import org.junit.Ignore;
import org.motechproject.ghana.telco.BaseSpringTestContext;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class AllProgramMessageAuditsTest extends BaseSpringTestContext {
    @Autowired
    private AllSMSAudits allProgramMessageAudits;

//    @Test
//    public void shouldGetAuditSortedByDate() {
//
//        SMSAudit programAudit = new SMSAudit("94500000", "Child Care", new DateTime(2011, 3, 2, 0, 0), "Message Sent");
//        SMSAudit programAudit2 = new SMSAudit("94500000", "Child Care", new DateTime(2011, 3, 1, 0, 0), "Message Sent");
//        SMSAudit programAudit3 = new SMSAudit("94500000", "Child Care", new DateTime(2011, 3, 6, 0, 0), "Message Sent");
//        SMSAudit programAudit4 = new SMSAudit("94500000", "Child Care", new DateTime(2011, 9, 2, 0, 0), "Message Sent");
//        SMSAudit programAudit5 = new SMSAudit("94500000", "Child Care", new DateTime(2011, 8, 2, 0, 0), "Message Sent");
//
//        addAndMarkForDeletion(allProgramMessageAudits, programAudit);
//        addAndMarkForDeletion(allProgramMessageAudits, programAudit2);
//        addAndMarkForDeletion(allProgramMessageAudits, programAudit3);
//        addAndMarkForDeletion(allProgramMessageAudits, programAudit4);
//        addAndMarkForDeletion(allProgramMessageAudits, programAudit5);
//
//        List<SMSAudit> messageAuditList = allProgramMessageAudits.sortedByDate();
//        assertEquals(asList(programAudit2, programAudit, programAudit3, programAudit5, programAudit4), messageAuditList);
//    }

}
