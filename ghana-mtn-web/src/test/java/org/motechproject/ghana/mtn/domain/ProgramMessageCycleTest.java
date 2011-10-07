package org.motechproject.ghana.mtn.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ProgramMessageCycleTest {

    @Test
    public void shouldGetNearestCycleDateBasedOnCurrentDayOfWeek() {

        assertEquals(new DateTime(2011, 10, 3, 0, 0), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 1, 0, 0)));
        assertEquals(new DateTime(2011, 10, 3, 2, 0), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 2, 2, 0)));
        assertEquals(new DateTime(2011, 10, 3, 0, 0), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 3, 0, 0)));
        assertEquals(new DateTime(2011, 10, 5, 5, 0), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 4, 5, 0)));
        assertEquals(new DateTime(2011, 10, 5, 6, 0), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 5, 6, 0)));
        assertEquals(new DateTime(2011, 10, 7, 3, 0), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 6, 3, 0)));
        assertEquals(new DateTime(2011, 10, 7, 0, 1), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 7, 0, 1)));
        assertEquals(new DateTime(2011, 10, 10, 0, 3), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 8, 0, 3)));
        assertEquals(new DateTime(2011, 10, 10, 0, 4), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 9, 0, 4)));
        assertEquals(new DateTime(2011, 10, 10, 5, 5), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 10, 5, 5)));
        assertEquals(new DateTime(2011, 10, 12, 0, 6), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 11, 0, 6)));
        assertEquals(new DateTime(2011, 10, 12, 0, 6), new ProgramMessageCycle().nearestCycleDate(new DateTime(2011, 10, 12, 0, 6)));
    }
}
