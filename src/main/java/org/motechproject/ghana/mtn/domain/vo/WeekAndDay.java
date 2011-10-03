package org.motechproject.ghana.mtn.domain.vo;

public class WeekAndDay {
    private Week week;
    private Day day;

    public WeekAndDay() {
    }

    public WeekAndDay(Week week, Day day) {
        this.week = week;
        this.day = day;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
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
