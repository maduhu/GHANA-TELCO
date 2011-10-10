package org.motechproject.ghana.mtn.billing.service;

import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.BillingCycleRequest;
import org.motechproject.ghana.mtn.billing.mock.MTNMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceImpl implements BillingService {
    private MTNMock mtnMock;
    private AllBillAccounts allBillAccounts;
    private BillingScheduler scheduler;
    private BillingAuditor auditor;

    public static final String BILLING_SCHEDULE_CREATED = "Billing success and schedule created";

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
        Double fee = request.getFeeForProgram();
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
    public BillingServiceResponse chargeProgramFee(BillingServiceRequest request) {
        String mobileNumber = request.getMobileNumber();
        Double fee = request.getFeeForProgram();
        Double balance = mtnMock.getBalanceFor(mobileNumber);
        IProgramType programType = request.getProgramType();

        mtnMock.chargeCustomer(mobileNumber, fee);
        auditor.audit(request);
        allBillAccounts.updateFor(mobileNumber, balance, programType);
        return new BillingServiceResponse();
    }

    @Override
    public BillingServiceResponse processRegistration(BillingCycleRequest billingCycleRequest) {
        BillingServiceResponse response = chargeProgramFee(billingCycleRequest);
        if (response.hasErrors()) return response;
        scheduler.startFor(billingCycleRequest);
        return new BillingServiceResponse<String>(BILLING_SCHEDULE_CREATED);
    }

    private BillingServiceResponse responseFor(ValidationError error) {
        BillingServiceResponse response = new BillingServiceResponse();
        response.addError(error);
        return response;
    }

}

