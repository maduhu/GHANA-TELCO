package org.motechproject.ghana.mtn.billing.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.domain.BillStatus;
import org.motechproject.ghana.mtn.vo.Money;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

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
        BillAudit billAudit = new BillAudit("1234567890", new Money(2D), BillStatus.SUCCESS, null, DateUtil.today());
        allBillAudits.add(billAudit);
        assertEquals(allBillAudits.getAll().size(), 1);
    }

}