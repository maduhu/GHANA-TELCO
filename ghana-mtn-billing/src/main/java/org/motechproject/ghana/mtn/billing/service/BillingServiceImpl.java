package org.motechproject.ghana.mtn.billing.service;

import org.drools.core.util.StringUtils;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.domain.BillStatus;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.ValidationError;
import org.motechproject.ghana.mtn.billing.mock.MTNBillingSystemMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.billing.repository.AllBillAudits;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceImpl implements BillingService {
    private MTNBillingSystemMock mtnBillingSystemMock;
    private AllBillAudits allBillAudits;
    private AllBillAccounts allBillAccounts;

    @Autowired
    public BillingServiceImpl(MTNBillingSystemMock mtnBillingSystemMock, AllBillAudits allBillAudits, AllBillAccounts allBillAccounts) {
        this.mtnBillingSystemMock = mtnBillingSystemMock;
        this.allBillAudits = allBillAudits;
        this.allBillAccounts = allBillAccounts;
    }

    @Override
    public BillingServiceResponse chargeSubscriptionFee(BillingServiceRequest billingServiceRequest) {
        String mobileNumber = billingServiceRequest.getMobileNumber();
        double fee = billingServiceRequest.getProgramType().getFee();
        Double availableBalance = mtnBillingSystemMock.getAvailableBalance(mobileNumber);
        if (!isMTNCustomer(mobileNumber)) {
            updateUserAccountAndPersistAudit(billingServiceRequest, BillStatus.FAILURE, ValidationError.NOT_A_VALID_CUSTOMER.name(), availableBalance);
            return notAMtnCustomerResponse();
        }
        if (!hasSufficientFundForProgram(fee, availableBalance)) {
            updateUserAccountAndPersistAudit(billingServiceRequest, BillStatus.FAILURE, ValidationError.INSUFFICIENT_FUND.name(), availableBalance);
            return noSufficientFundResponse();
        }
        mtnBillingSystemMock.chargeCustomer(mobileNumber, fee);
        updateUserAccountAndPersistAudit(billingServiceRequest, BillStatus.SUCCESS, StringUtils.EMPTY, availableBalance);
        return new BillingServiceResponse();
    }

    private void updateUserAccountAndPersistAudit(BillingServiceRequest billingServiceRequest, BillStatus billStatus, String failureReason, Double availableBalance) {
        String mobileNumber = billingServiceRequest.getMobileNumber();
        allBillAudits.add(new BillAudit(mobileNumber,billingServiceRequest.getProgramType().getFee(), billStatus, failureReason, DateUtil.today()));
        if (billStatus.equals(BillStatus.SUCCESS))
            allBillAccounts.updateBillAccount(mobileNumber, availableBalance, billingServiceRequest.getProgramType());
    }

    private BillingServiceResponse noSufficientFundResponse() {
        BillingServiceResponse billingServiceResponse = new BillingServiceResponse();
        billingServiceResponse.addValidationError(ValidationError.INSUFFICIENT_FUND);
        return billingServiceResponse;
    }

    private BillingServiceResponse notAMtnCustomerResponse() {
        BillingServiceResponse billingServiceResponse = new BillingServiceResponse();
        billingServiceResponse.addValidationError(ValidationError.NOT_A_VALID_CUSTOMER);
        return billingServiceResponse;
    }

    private boolean hasSufficientFundForProgram(double fee, Double availableBalance) {
        return availableBalance >= fee;
    }

    private boolean isMTNCustomer(String mobileNumber) {
        return mtnBillingSystemMock.isMtnCustomer(mobileNumber);
    }
}

