package org.motechproject.ghana.mtn.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.springframework.test.util.ReflectionTestUtils;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.model.DayOfWeek.*;

public class ProgramMessageCycleTest {

    @Mock
    private Subscription subscription;
    @Mock
    private AllMessageCampaigns allMessageCampaigns;
    private ProgramMessageCycle programMessageCycle;

    @Before
    public void setUp() {
        initMocks(this);
        programMessageCycle = new ProgramMessageCycle();
        ReflectionTestUtils.setField(programMessageCycle, "allMessageCampaigns", allMessageCampaigns);
        when(subscription.programKey()).thenReturn(ProgramType.PREGNANCY);
        when(allMessageCampaigns.getApplicableDaysForRepeatingCampaign(ProgramType.PREGNANCY, "Pregnancy Message")).thenReturn(asList(Monday, Wednesday, Friday));
    }

    @Test
    public void shouldReturnStartOfCycleDate_IfGivenDateExactlyFallsOnCycleEndOrAfter() {

        DateTime thuFeb232012 = new DateTime(2012, 2, 23, 0, 1);
        assertNearestCycleDate(thuFeb232012, new DateTime(2012, 2, 24, 0, 1));

        DateTime friFeb242012 = new DateTime(2012, 2, 24, 0, 1);
        assertNearestCycleDate(friFeb242012, new DateTime(2012, 2, 27, 0, 1));

        DateTime satFeb252012 = new DateTime(2012, 2, 25, 0, 2);
        assertNearestCycleDate(satFeb252012, new DateTime(2012, 2, 27, 0, 2));

        DateTime sunFeb262012 = new DateTime(2012, 2, 26, 10, 0);
        assertNearestCycleDate(sunFeb262012, new DateTime(2012, 2, 27, 10, 0));

        DateTime monFeb272012 = new DateTime(2012, 2, 27, 0, 10);
        assertNearestCycleDate(monFeb272012, new DateTime(2012, 2, 29, 0, 10));
    }

    @Test
    public void shouldGetNearestCycleDateBasedOnCurrentDayOfWeek() {

        DateTime oct1Sat = new DateTime(2011, 10, 1, 0, 0);
        DateTime oct2Sun = new DateTime(2011, 10, 2, 2, 0);
        DateTime oct3Mon = new DateTime(2011, 10, 3, 0, 0);
        DateTime oct4Tue = new DateTime(2011, 10, 4, 5, 0);
        DateTime oct5Wed = new DateTime(2011, 10, 5, 6, 0);
        DateTime oct6Thu = new DateTime(2011, 10, 6, 3, 0);
        DateTime oct7Fri = new DateTime(2011, 10, 7, 0, 1);
        DateTime oct8Sat = new DateTime(2011, 10, 8, 0, 3);
        DateTime oct9Sun = new DateTime(2011, 10, 9, 0, 4);
        DateTime oct10Mon = new DateTime(2011, 10, 10, 5, 5);
        DateTime oct11Tue = new DateTime(2011, 10, 11, 0, 6);
        DateTime oct12Wed = new DateTime(2011, 10, 12, 0, 6);
        DateTime oct13Thu = new DateTime(2011, 10, 13, 0, 6);
        DateTime oct14Fri = new DateTime(2011, 10, 14, 0, 6);

        assertNearestCycleDate(oct1Sat, new DateTime(2011, 10, 3, 0, 0));
        assertNearestCycleDate(oct2Sun, new DateTime(2011, 10, 3, 2, 0));
        assertNearestCycleDate(oct3Mon, new DateTime(2011, 10, 5, 0, 0));
        assertNearestCycleDate(oct4Tue, new DateTime(2011, 10, 5, 5, 0));
        assertNearestCycleDate(oct5Wed, new DateTime(2011, 10, 7, 6, 0));
        assertNearestCycleDate(oct6Thu, new DateTime(2011, 10, 7, 3, 0));

        assertNearestCycleDate(oct7Fri, new DateTime(2011, 10, 10, 0, 1));
        assertNearestCycleDate(oct8Sat, new DateTime(2011, 10, 10, 0, 3));
        assertNearestCycleDate(oct9Sun, new DateTime(2011, 10, 10, 0, 4));
        assertNearestCycleDate(oct10Mon, new DateTime(2011, 10, 12, 5, 5));
        assertNearestCycleDate(oct11Tue, new DateTime(2011, 10, 12, 0, 6));
        assertNearestCycleDate(oct12Wed, new DateTime(2011, 10, 14, 0, 6));
        assertNearestCycleDate(oct13Thu, new DateTime(2011, 10, 14, 0, 6));
        assertNearestCycleDate(oct14Fri, new DateTime(2011, 10, 17, 0, 6));
    }

    private void assertNearestCycleDate(DateTime dateTime, DateTime expectedDateTime) {
        when(subscription.getRegistrationDate()).thenReturn(dateTime);
        assertEquals(expectedDateTime, programMessageCycle.nearestCycleDate(subscription));
    }
}
