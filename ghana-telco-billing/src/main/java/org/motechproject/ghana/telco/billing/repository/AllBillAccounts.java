package org.motechproject.ghana.telco.billing.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.telco.billing.domain.BillAccount;
import org.motechproject.ghana.telco.domain.IProgramType;
import org.motechproject.ghana.telco.vo.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllBillAccounts extends MotechBaseRepository<BillAccount> {

   @Autowired
   protected AllBillAccounts(@Qualifier("billingDbConnector") CouchDbConnector db) {
       super(BillAccount.class, db);
   }

    public void updateFor(String mobileNumber, Double currentBalance, IProgramType programType) {
       BillAccount billAccount = findByMobileNumber(mobileNumber);
       if (billAccount == null)
           billAccount = new BillAccount();

       billAccount.setMobileNumber(mobileNumber);
       billAccount.setCurrentBalance(new Money(currentBalance));
       billAccount.setProgramFee(programType.getProgramKey(), programType.getFee());

       if (null == billAccount.getId())
           add(billAccount);
       else
           update(billAccount);
   }

   @View(name = "find_by_mobile_number", map = "function(doc) { if(doc.type === 'BillAccount') emit(doc.mobileNumber, doc) }")
   public BillAccount findByMobileNumber(String mobileNumber) {
       ViewQuery viewQuery = createQuery("find_by_mobile_number").key(mobileNumber);
       List<BillAccount> billAccounts = db.queryView(viewQuery, BillAccount.class);
       return billAccounts.isEmpty() ? null : billAccounts.get(0);
   }
}