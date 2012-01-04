package org.motechproject.ghana.telco.utils;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DateUtilsTest {

    DateUtils dateUtils = new DateUtils();

    @Test
    public void shouldTestOrdinalForAllDaysOfMonth() {
       assertThat(dateUtils.dayWithOrdinal(1), is("1st"));
       assertThat(dateUtils.dayWithOrdinal(2), is("2nd"));
       assertThat(dateUtils.dayWithOrdinal(3), is("3rd"));
       assertThat(dateUtils.dayWithOrdinal(4), is("4th"));
       assertThat(dateUtils.dayWithOrdinal(5), is("5th"));
       assertThat(dateUtils.dayWithOrdinal(6), is("6th"));
       assertThat(dateUtils.dayWithOrdinal(7), is("7th"));
       assertThat(dateUtils.dayWithOrdinal(8), is("8th"));
       assertThat(dateUtils.dayWithOrdinal(9), is("9th"));
       assertThat(dateUtils.dayWithOrdinal(10), is("10th"));
       assertThat(dateUtils.dayWithOrdinal(11), is("11th"));
       assertThat(dateUtils.dayWithOrdinal(12), is("12th"));
       assertThat(dateUtils.dayWithOrdinal(13), is("13th"));
       assertThat(dateUtils.dayWithOrdinal(14), is("14th"));
       assertThat(dateUtils.dayWithOrdinal(15), is("15th"));
       assertThat(dateUtils.dayWithOrdinal(16), is("16th"));
       assertThat(dateUtils.dayWithOrdinal(17), is("17th"));
       assertThat(dateUtils.dayWithOrdinal(18), is("18th"));
       assertThat(dateUtils.dayWithOrdinal(19), is("19th"));
       assertThat(dateUtils.dayWithOrdinal(20), is("20th"));
       assertThat(dateUtils.dayWithOrdinal(21), is("21st"));
       assertThat(dateUtils.dayWithOrdinal(22), is("22nd"));
       assertThat(dateUtils.dayWithOrdinal(23), is("23rd"));
       assertThat(dateUtils.dayWithOrdinal(24), is("24th"));
       assertThat(dateUtils.dayWithOrdinal(25), is("25th"));
       assertThat(dateUtils.dayWithOrdinal(26), is("26th"));
       assertThat(dateUtils.dayWithOrdinal(27), is("27th"));
       assertThat(dateUtils.dayWithOrdinal(28), is("28th"));
       assertThat(dateUtils.dayWithOrdinal(29), is("29th"));
       assertThat(dateUtils.dayWithOrdinal(30), is("30th"));
       assertThat(dateUtils.dayWithOrdinal(31), is("31st"));
    }
}
