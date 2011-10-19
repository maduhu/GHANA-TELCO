package org.motechproject.ghana.mtn.repository;


import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.mtn.BaseIntegrationTest;
import org.motechproject.ghana.mtn.domain.ProgramMessageAudit;
import org.motechproject.model.MotechAuditableDataObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@Ignore
public class AllProgramMessageAuditsTest extends BaseIntegrationTest {
    @Autowired
    private AllProgramMessageAudits allProgramMessageAudits;

    @Test
    public void shouldGetAuditSortedByDate() {

        ProgramMessageAudit programAudit = new ProgramMessageAudit("94500000", "Child Care", new DateTime(2011, 3, 2, 0, 0), "Message Sent");
        ProgramMessageAudit programAudit2 = new ProgramMessageAudit("94500000", "Child Care", new DateTime(2011, 3, 1, 0, 0), "Message Sent");
        ProgramMessageAudit programAudit3 = new ProgramMessageAudit("94500000", "Child Care", new DateTime(2011, 3, 6, 0, 0), "Message Sent");
        ProgramMessageAudit programAudit4 = new ProgramMessageAudit("94500000", "Child Care", new DateTime(2011, 9, 2, 0, 0), "Message Sent");
        ProgramMessageAudit programAudit5 = new ProgramMessageAudit("94500000", "Child Care", new DateTime(2011, 8, 2, 0, 0), "Message Sent");

        addAndMarkForDeletion(allProgramMessageAudits, programAudit);
        addAndMarkForDeletion(allProgramMessageAudits, programAudit2);
        addAndMarkForDeletion(allProgramMessageAudits, programAudit3);
        addAndMarkForDeletion(allProgramMessageAudits, programAudit4);
        addAndMarkForDeletion(allProgramMessageAudits, programAudit5);

        List<ProgramMessageAudit> messageAuditList = allProgramMessageAudits.sortedByDate();
        assertEquals(asList(programAudit2, programAudit, programAudit3, programAudit5, programAudit4), messageAuditList);
    }

    private void addAndMarkForDeletion(MotechAuditableRepository repository, MotechAuditableDataObject auditableDataObject) {
        repository.add(auditableDataObject);
        markForDeletion(auditableDataObject);
    }
}
