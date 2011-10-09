package org.motechproject.ghana.mtn.billing.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.mtn.billing.mock.MTNMock;
import org.motechproject.ghana.mtn.billing.repository.AllBillAccounts;
import org.motechproject.ghana.mtn.billing.service.BillingAuditor;
import org.motechproject.ghana.mtn.billing.service.BillingScheduler;
import org.motechproject.ghana.mtn.billing.service.BillingServiceImpl;
import org.motechproject.scheduler.MotechSchedulerService;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class BillingServiceImplTest {
    private BillingServiceImpl service;
    @Mock
    private MTNMock mtnMock;
    @Mock
    private AllBillAccounts allBillAccounts;
    @Mock
    private MotechSchedulerService schedulerService;
    @Mock
    private BillingScheduler scheduler;
    @Mock
    private BillingAuditor auditor;

    @Before
    public void setUp() {
        initMocks(this);
        service = new BillingServiceImpl(allBillAccounts, scheduler, auditor, mtnMock);
    }

    @Test
    public void shouldTest() {
        assertTrue(true);
    }

}