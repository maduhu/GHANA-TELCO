package org.motechproject.ghana.telco.domain.vo;

import org.motechproject.model.DayOfWeek;

public class WeekAndDay {
    private Week week;
    private DayOfWeek day;

    public WeekAndDay() {
    }

    public WeekAndDay(Week week, DayOfWeek day) {
        this.week = week;
        this.day = day;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public boolean isSameAs(WeekAndDay weekAndDay) {
        return week.equals(weekAndDay.getWeek()) && day.equals(weekAndDay.getDay());
    }

    public boolean isBefore(WeekAndDay weekAndDay) {
        return week.getNumber() <= weekAndDay.getWeek().getNumber() && day.compareTo(weekAndDay.getDay()) <= 0;
    }
}
