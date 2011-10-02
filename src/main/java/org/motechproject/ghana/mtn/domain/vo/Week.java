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

}
