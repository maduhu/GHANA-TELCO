package org.motechproject.ghana.telco.billing.service;

import org.apache.log4j.Logger;
import org.motechproject.ghana.telco.billing.dto.*;
import org.motechproject.ghana.telco.billing.exception.InsufficientFundsException;
import org.motechproject.ghana.telco.billing.mock.TelcoMock;
import org.motechproject.ghana.telco.billing.repository.AllBillAccounts;
import org.motechproject.ghana.telco.domain.IProgramType;
import org.motechproject.ghana.telco.validation.ValidationError;
import org.motechproject.ghana.telco.vo.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.motechproject.ghana.telco.validation.ValidationError.INSUFFICIENT_FUNDS;
import static org.motechproject.ghana.telco.validation.ValidationError.INSUFFICIENT_FUNDS_DURING_REGISTRATION;

@Service
public class BillingServiceImpl implements BillingService {
    private TelcoMock telcoMock;
    private AllBillAccounts allBillAccounts;
    private BillingScheduler scheduler;
    private BillingAuditor auditor;

    public static final String BILLING_SCHEDULE_STARTED = "Billing schedule started";
    public static final String BILLING_SCHEDULE_STOPPED = "Billing schedule stopped";
    public static final String BILLING_SUCCESSFUL = "Billing Successful";
    public static final String BILLING_ROLLED_OVER = "Billing Rollover Completed";
    private Logger log = Logger.getLogger(BillingServiceImpl.class);

    @Autowired
    public BillingServiceImpl(AllBillAccounts allBillAccounts, BillingScheduler scheduler, BillingAuditor auditor, TelcoMock telcoMock) {
        this.allBillAccounts = allBillAccounts;
        this.scheduler = scheduler;
        this.telcoMock = telcoMock;
        this.auditor = auditor;
    }

    @Override
    public BillingServiceResponse checkIfUserHasFunds(BillingServiceRequest request) {
        String mobileNumber = request.getMobileNumber();
        Double fee = request.getProgramFeeValue();
        if (!telcoMock.isTelcoCustomer(mobileNumber)) {
            auditor.auditError(request, ValidationError.INVALID_CUSTOMER);
            return responseFor(ValidationError.INVALID_CUSTOMER);
        }
        Double balance = telcoMock.getBalanceFor(mobileNumber);
        if (balance < fee) {
            auditor.auditError(request, INSUFFICIENT_FUNDS);
            return responseFor(INSUFFICIENT_FUNDS);
        }
        return new BillingServiceResponse();
    }

    @Override
    public BillingServiceResponse<CustomerBill> chargeProgramFee(BillingServiceRequest request) {
        return chargeFee(request, INSUFFICIENT_FUNDS);
    }

    @Override
    public BillingServiceResponse<CustomerBill> chargeAndStartBilling(BillingCycleRequest request) {
        BillingServiceResponse<CustomerBill> response = chargeFee(request, INSUFFICIENT_FUNDS_DURING_REGISTRATION);
        if (response.hasErrors()) return response;
        scheduler.startFor(request);
        return new BillingServiceResponse<CustomerBill>(new CustomerBill(BILLING_SCHEDULE_STARTED, response.getValue().getAmountCharged()));
    }
    
    @Override
    public BillingServiceResponse<String> startBilling(BillingCycleRequest request) {
        scheduler.startFor(request);
        return new BillingServiceResponse<String>(BILLING_SCHEDULE_STARTED);
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

    private BillingServiceResponse<CustomerBill> chargeFee(BillingServiceRequest request,  ValidationError insufficientFunds) {
        String mobileNumber = request.getMobileNumber();
        Double fee = request.getProgramFeeValue();
        IProgramType programType = request.getProgramType();

        Double balance = telcoMock.getBalanceFor(mobileNumber);
        Money chargedAmount = null;
        try {
            chargedAmount = telcoMock.chargeCustomer(mobileNumber, fee);
        } catch (InsufficientFundsException e) {
            log.debug("Insufficient Funds for " + mobileNumber);
            BillingServiceResponse<CustomerBill> response = new BillingServiceResponse<CustomerBill>();
            auditor.auditError(request, insufficientFunds);
            return response.addError(insufficientFunds);
        }

        auditor.audit(request);
        allBillAccounts.updateFor(mobileNumber, balance, programType);
        return new BillingServiceResponse<CustomerBill>(new CustomerBill(BILLING_SUCCESSFUL, chargedAmount));
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
