package org.motechproject.ghana.mtn.domain.vo;

public class Week {
    private Integer value;

    public Week(Integer value) {
        this.value = value;
    }

    public boolean is(Integer inputValue) {
        return value.equals(inputValue);
    }
}
