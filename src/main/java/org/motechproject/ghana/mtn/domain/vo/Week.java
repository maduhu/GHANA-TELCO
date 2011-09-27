package org.motechproject.ghana.mtn.domain.vo;

public class Week {
    private Integer number;

    public Week(Integer number) {
        this.number = number;
    }

    public boolean is(Integer value) {
        return number.equals(value);
    }

    public Integer number() {
        return number;
    }
}
