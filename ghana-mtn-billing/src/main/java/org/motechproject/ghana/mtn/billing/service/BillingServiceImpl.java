package org.motechproject.ghana.mtn.billing.service;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.billing.dto.*;
import org.motechproject.ghana.mtn.billing.exception.InsufficientFundsException;
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
    public static final String BILLING_SCHEDULE_STOPPED = "Billing schedule stopped";
    public static final String BILLING_SUCCESSFUL = "Billing Successful";
    public static final String BILLING_ROLLED_OVER = "Billing Rollover Completed";
    private Logger log = Logger.getLogger(BillingServiceImpl.class);

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
        IProgramType programType = request.getProgramType();

        Double balance = mtnMock.getBalanceFor(mobileNumber);
        Money chargedAmount = null;
        try {
            chargedAmount = mtnMock.chargeCustomer(mobileNumber, fee);
        } catch (InsufficientFundsException e) {
            log.debug("Insufficient Funds for " + mobileNumber);
            BillingServiceResponse<CustomerBill> response = new BillingServiceResponse<CustomerBill>();
            response.addError(ValidationError.INSUFFICIENT_FUNDS);
            return response;
        }

        auditor.audit(request);
        allBillAccounts.updateFor(mobileNumber, balance, programType);
        return new BillingServiceResponse<CustomerBill>(new CustomerBill(BILLING_SUCCESSFUL, chargedAmount));
    }

    @Override
    public BillingServiceResponse<CustomerBill> chargeAndStartBilling(BillingCycleRequest request) {
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

    @Override
    public BillingServiceResponse rollOverBilling(BillingCycleRollOverRequest billingCycleRollOverRequest) {
        BillingServiceResponse stopBillingResponse = stopBilling(billingCycleRollOverRequest.getFromRequest());
        if (stopBillingResponse.hasErrors()) return stopBillingResponse;

        scheduler.startFor(billingCycleRollOverRequest.getToRequest());
        return new BillingServiceResponse<String>(BILLING_ROLLED_OVER);
    }

    @Override
    public BillingServiceResponse stopDefaultedBillingSchedule(DefaultedBillingRequest defaultedBillingRequest) {
        scheduler.stop(defaultedBillingRequest);
        return new BillingServiceResponse<String>(BILLING_SCHEDULE_STOPPED);
    }

    private BillingServiceResponse responseFor(ValidationError error) {
        BillingServiceResponse response = new BillingServiceResponse();
        response.addError(error);
        return response;
    }

    public BillingServiceResponse startDefaultedBillingSchedule(DefaultedBillingRequest request) {
        scheduler.startDefaultedBillingSchedule(request);
        return new BillingServiceResponse();
    }
}
