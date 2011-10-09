package org.motechproject.ghana.mtn.billing.service;

import org.apache.log4j.Logger;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceRequest;
import org.motechproject.ghana.mtn.billing.dto.BillingServiceResponse;
import org.motechproject.ghana.mtn.billing.dto.RegistrationBillingRequest;
import org.motechproject.ghana.mtn.billing.mock.MTNMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.domain.IProgramType;
import org.motechproject.ghana.mtn.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceImpl implements BillingService {
    private static final Logger log = Logger.getLogger(BillingServiceImpl.class);

    private MTNMock mtnMock;
    private AllBillAccounts allBillAccounts;
    private BillingScheduler scheduler;
    private BillingAuditor auditor;

    @Autowired
    public BillingServiceImpl(AllBillAccounts allBillAccounts, BillingScheduler scheduler, BillingAuditor auditor, MTNMock mtnMock) {
        this.allBillAccounts = allBillAccounts;
        this.scheduler = scheduler;
        this.mtnMock = mtnMock;
        this.auditor = auditor;
    }

    @Override
    public BillingServiceResponse hasFundsForProgram(BillingServiceRequest request) {
        String mobileNumber = request.getMobileNumber();
        Double fee = request.getFeeForProgram();
        Double balance = mtnMock.getBalanceFor(mobileNumber);

        if (!mtnMock.isMtnCustomer(mobileNumber)) {
            auditor.auditError(request, ValidationError.NOT_A_VALID_CUSTOMER);
            return responseFor(ValidationError.NOT_A_VALID_CUSTOMER);
        }
        if (balance <= fee) {
            auditor.auditError(request, ValidationError.INSUFFICIENT_FUND);
            return responseFor(ValidationError.INSUFFICIENT_FUND);
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
    public BillingServiceResponse processRegistration(RegistrationBillingRequest request) {
        BillingServiceResponse response = chargeProgramFee(request);
        if (response.hasErrors()) return response;
        scheduler.createFor(request);
        return new BillingServiceResponse<String>("Billing success and schedule created");
    }

    private BillingServiceResponse responseFor(ValidationError error) {
        BillingServiceResponse response = new BillingServiceResponse();
        response.addError(error);
        return response;
    }

}

