package org.motechproject.ghana.mtn.billing.service;

import org.apache.log4j.Logger;
import org.drools.core.util.StringUtils;
import org.motechproject.ghana.mtn.billing.domain.BillAudit;
import org.motechproject.ghana.mtn.billing.domain.BillStatus;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.mock.MTNBillingSystemMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.billing.repository.AllBillAudits;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BillingServiceImpl implements BillingService {
    private MTNBillingSystemMock mtnBillingSystemMock;
    private AllBillAudits allBillAudits;
    private AllBillAccounts allBillAccounts;
    private static final Logger log = Logger.getLogger(BillingServiceImpl.class);

    @Autowired
    public BillingServiceImpl(MTNBillingSystemMock mtnBillingSystemMock, AllBillAudits allBillAudits, AllBillAccounts allBillAccounts) {
        this.mtnBillingSystemMock = mtnBillingSystemMock;
        this.allBillAudits = allBillAudits;
        this.allBillAccounts = allBillAccounts;
    }

    @Override
    public BillingServiceResponse chargeSubscriptionFee(BillingServiceRequest billingServiceRequest) {
        String mobileNumber = billingServiceRequest.getMobileNumber();
        double fee = billingServiceRequest.getProgramType().getFee().getValue();
        Double availableBalance = null;
        try {
            availableBalance = mtnBillingSystemMock.getAvailableBalance(mobileNumber);
        } catch (IOException e) {
            log.error(e);
        }
        mtnBillingSystemMock.chargeCustomer(mobileNumber, fee);
        updateUserAccountAndPersistAudit(billingServiceRequest, BillStatus.SUCCESS, StringUtils.EMPTY, availableBalance);
        return new BillingServiceResponse();
    }

    @Override
    public BillingServiceResponse hasAvailableFundForProgram(BillingServiceRequest billingServiceRequest) {
        String mobileNumber = billingServiceRequest.getMobileNumber();
        double fee = billingServiceRequest.getProgramType().getFee().getValue();
        Double availableBalance = null;
        try {
            availableBalance = mtnBillingSystemMock.getAvailableBalance(mobileNumber);
        } catch (IOException e) {
            log.error(e);
        }
        if (!isMTNCustomer(mobileNumber)) {
            updateUserAccountAndPersistAudit(billingServiceRequest, BillStatus.FAILURE, ValidationError.NOT_A_VALID_CUSTOMER.name(), availableBalance);
            return notAMtnCustomerResponse();
        }
        if (!hasSufficientFundForProgram(fee, availableBalance)) {
            updateUserAccountAndPersistAudit(billingServiceRequest, BillStatus.FAILURE, ValidationError.INSUFFICIENT_FUND.name(), availableBalance);
            return noSufficientFundResponse();
        }
        return new BillingServiceResponse();
    }

    private void updateUserAccountAndPersistAudit(BillingServiceRequest billingServiceRequest, BillStatus billStatus, String failureReason, Double availableBalance) {
        String mobileNumber = billingServiceRequest.getMobileNumber();
        allBillAudits.add(new BillAudit(mobileNumber,billingServiceRequest.getProgramType().getFee().getValue(), billStatus, failureReason, DateUtil.today()));
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
        try {
            return mtnBillingSystemMock.isMtnCustomer(mobileNumber);
        } catch (IOException e) {
            log.error(e);
        }
        return false;
    }
}

