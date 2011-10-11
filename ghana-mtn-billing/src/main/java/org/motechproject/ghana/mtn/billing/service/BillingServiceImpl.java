package org.motechproject.ghana.mtn.billing.service;

import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.dto.CustomerBill;
import org.motechproject.ghana.mtn.billing.mock.MTNMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.motechproject.ghana.mtn.vo.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceImpl implements BillingService {
    private MTNMock mtnMock;
    private AllBillAccounts allBillAccounts;
    private BillingScheduler scheduler;
    private BillingAuditor auditor;

    public static final String BILLING_SCHEDULE_STARTED = "Billing schedule started";
    public static final String BILLING_SUCCESSFUL = "Billing Successful";
    public static final String BILLING_SCHEDULE_STOPPED = "Billing schedule stopped";

    @Autowired
    public BillingServiceImpl(AllBillAccounts allBillAccounts, BillingScheduler scheduler, BillingAuditor auditor, MTNMock mtnMock) {
        this.allBillAccounts = allBillAccounts;
        this.scheduler = scheduler;
        this.mtnMock = mtnMock;
        this.auditor = auditor;
    }

    @Override
    public BillingServiceResponse checkIfUserHasFunds(BillingServiceRequest request) {
        String mobileNumber = request.getMobileNumber();
        Double fee = request.getProgramFeeValue();
        if (!mtnMock.isMtnCustomer(mobileNumber)) {
            auditor.auditError(request, ValidationError.INVALID_CUSTOMER);
            return responseFor(ValidationError.INVALID_CUSTOMER);
        }
        Double balance = mtnMock.getBalanceFor(mobileNumber);
        if (balance < fee) {
            auditor.auditError(request, ValidationError.INSUFFICIENT_FUNDS);
            return responseFor(ValidationError.INSUFFICIENT_FUNDS);
        }
        return new BillingServiceResponse();
    }

    @Override
    public BillingServiceResponse<CustomerBill> chargeProgramFee(BillingServiceRequest request) {
        String mobileNumber = request.getMobileNumber();
        Double fee = request.getProgramFeeValue();
        Double balance = mtnMock.getBalanceFor(mobileNumber);
        IProgramType programType = request.getProgramType();

        Money chargedAmount = mtnMock.chargeCustomer(mobileNumber, fee);
        auditor.audit(request);
        allBillAccounts.updateFor(mobileNumber, balance, programType);
        return new BillingServiceResponse<CustomerBill>(new CustomerBill(BILLING_SUCCESSFUL, chargedAmount));
    }

    @Override
    public BillingServiceResponse<CustomerBill> startBilling(BillingCycleRequest request) {
        BillingServiceResponse<CustomerBill> response = chargeProgramFee(request);
        if (response.hasErrors()) return response;
        scheduler.startFor(request);
        return new BillingServiceResponse<CustomerBill>(new CustomerBill(BILLING_SCHEDULE_STARTED, response.getValue().getAmountCharged()));
    }

    @Override
    public BillingServiceResponse stopBilling(BillingCycleRequest request) {
        scheduler.stopFor(request);
        return new BillingServiceResponse<String>(BILLING_SCHEDULE_STOPPED);
    }

    private BillingServiceResponse responseFor(ValidationError error) {
        BillingServiceResponse response = new BillingServiceResponse();
        response.addError(error);
        return response;
    }

}

