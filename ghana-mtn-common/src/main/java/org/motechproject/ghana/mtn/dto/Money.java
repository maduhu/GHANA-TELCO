package org.motechproject.ghana.mtn.dto;

import org.apache.commons.lang.builder.EqualsBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;
        return new EqualsBuilder()
            .append(this.value, money.getValue())
            .isEquals();        
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
