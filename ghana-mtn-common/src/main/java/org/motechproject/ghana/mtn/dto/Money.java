package org.motechproject.ghana.mtn.dto;

public class Money {
    private Double value;

    public Money() {
    }

    public Money(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
