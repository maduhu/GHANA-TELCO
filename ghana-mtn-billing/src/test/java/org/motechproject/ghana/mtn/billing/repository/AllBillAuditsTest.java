package org.motechproject.ghana.mtn.billing.repository;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.domain.BillStatus;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AllBillAuditsTest extends RepositoryTest {
   @Autowired
   private AllBillAudits allBillAudits;
   private BillAudit billAudit = new BillAudit("1234567890", 2D, BillStatus.SUCCESS, null, DateUtil.today());

   @Test
   public void ShouldAllBillAudit() {
       allBillAudits.add(billAudit);

       assertEquals(allBillAudits.getAll().size(), 1);
   }

   @After
   public void destroy() {
       for (BillAudit audit : allBillAudits.getAll()) {
           allBillAudits.remove(audit);
       }
   }

}