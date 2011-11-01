package org.motechproject.ghana.mtn.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ProgramMessageCycleTest {

    @Test
    public void shouldReturnStartOfCycleDate_IfGivenDateExactlyFallsOnCycleEndOrAfter() {

        DateTime thuFeb232012 = new DateTime(2012, 2, 23, 0, 1);
        DateTime friFeb242012 = new DateTime(2012, 2, 24, 0, 1);
        DateTime satFeb252012 = new DateTime(2012, 2, 25, 0, 2);
        DateTime sunFeb262012 = new DateTime(2012, 2, 26, 10, 0);
        DateTime monFeb272012 = new DateTime(2012, 2, 27, 0, 10);

        assertEquals(new DateTime(2012, 2, 24, 0, 1), new ProgramMessageCycle().nearestCycleDate(thuFeb232012));
        assertEquals(new DateTime(2012, 2, 27, 0, 1), new ProgramMessageCycle().nearestCycleDate(friFeb242012));
        assertEquals(new DateTime(2012, 2, 27, 0, 2), new ProgramMessageCycle().nearestCycleDate(satFeb252012));
        assertEquals(new DateTime(2012, 2, 27, 10, 0), new ProgramMessageCycle().nearestCycleDate(sunFeb262012));
        assertEquals(new DateTime(2012, 2, 29, 0, 10), new ProgramMessageCycle().nearestCycleDate(monFeb272012));
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

        assertEquals(new DateTime(2011, 10, 3, 0, 0), new ProgramMessageCycle().nearestCycleDate(oct1Sat));
        assertEquals(new DateTime(2011, 10, 3, 2, 0), new ProgramMessageCycle().nearestCycleDate(oct2Sun));
        assertEquals(new DateTime(2011, 10, 5, 0, 0), new ProgramMessageCycle().nearestCycleDate(oct3Mon));
        assertEquals(new DateTime(2011, 10, 5, 5, 0), new ProgramMessageCycle().nearestCycleDate(oct4Tue));
        assertEquals(new DateTime(2011, 10, 7, 6, 0), new ProgramMessageCycle().nearestCycleDate(oct5Wed));
        assertEquals(new DateTime(2011, 10, 7, 3, 0), new ProgramMessageCycle().nearestCycleDate(oct6Thu));

        DateTime fridayToMonOct10 = new DateTime(2011, 10, 10, 0, 1);
        assertEquals(fridayToMonOct10, new ProgramMessageCycle().nearestCycleDate(oct7Fri));
        assertEquals(new DateTime(2011, 10, 10, 0, 3), new ProgramMessageCycle().nearestCycleDate(oct8Sat));
        assertEquals(new DateTime(2011, 10, 10, 0, 4), new ProgramMessageCycle().nearestCycleDate(oct9Sun));
        assertEquals(new DateTime(2011, 10, 12, 5, 5), new ProgramMessageCycle().nearestCycleDate(oct10Mon));
        assertEquals(new DateTime(2011, 10, 12, 0, 6), new ProgramMessageCycle().nearestCycleDate(oct11Tue));
        assertEquals(new DateTime(2011, 10, 14, 0, 6), new ProgramMessageCycle().nearestCycleDate(oct12Wed));
        assertEquals(new DateTime(2011, 10, 14, 0, 6), new ProgramMessageCycle().nearestCycleDate(oct13Thu));

        DateTime fridayToMonOct17 = new DateTime(2011, 10, 17, 0, 6);
        assertEquals(fridayToMonOct17, new ProgramMessageCycle().nearestCycleDate(oct14Fri));
    }
}
