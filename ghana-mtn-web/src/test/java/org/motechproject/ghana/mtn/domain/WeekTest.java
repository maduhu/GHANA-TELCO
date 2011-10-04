package org.motechproject.ghana.mtn.domain;

import org.junit.Test;
import org.motechproject.ghana.mtn.domain.vo.Week;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WeekTest {

    @Test
    public void shouldAnswerForItsValue(){
        Week week = new Week(21);
        assertTrue(week.is(21));
        assertFalse(week.is(23));
    }
}
