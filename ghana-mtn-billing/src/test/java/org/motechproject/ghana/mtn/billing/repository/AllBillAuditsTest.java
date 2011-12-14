package org.motechproject.ghana.mtn.billing.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.domain.BillStatus;
import org.motechproject.ghana.mtn.vo.Money;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllBillAuditsTest extends RepositoryTest<BillAudit> {
    @Autowired
    private AllBillAudits allBillAudits;

    @Before
    public void setUp() {
        setRepository(allBillAudits);
        removeAll();
    }

    @Test
    public void ShouldAllBillAudit() {
        BillAudit billAudit = new BillAudit("1234567890", "Child Care", new Money(2D), BillStatus.SUCCESS, null);
        allBillAudits.add(billAudit);
        List<BillAudit> billAudits = allBillAudits.getAll();
        assertEquals(billAudits.size(), 1);
        assertEquals(billAudits.get(0).getProgram(), "Child Care");
    }

}