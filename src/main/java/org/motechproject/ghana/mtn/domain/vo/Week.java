package org.motechproject.ghana.mtn.domain.vo;

public class Week {
    private Integer number;

    public Week() {
    }

    public Week(Integer number) {
        this.number = number;
    }

    public boolean is(Integer value) {
        return number.equals(value);
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Week add(int weeks) {
        return new Week(number + weeks);
    }

    @Override
    public String toString() {
        return "WEEK " + number.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Week week = (Week) o;
        if (number != null ? !number.equals(week.number) : week.number != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return number != null ? number.hashCode() : 0;
    }
}
