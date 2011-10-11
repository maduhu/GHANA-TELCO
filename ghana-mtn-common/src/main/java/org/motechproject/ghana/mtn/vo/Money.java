package org.motechproject.ghana.mtn.vo;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Currency;
import java.util.Locale;

public class Money {
    private Double value;
    private Currency currency;
    public Money() {
    }

    public Money(Double value) {
        this.value = value;
        this.currency = Currency.getInstance(Locale.getDefault());
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getValue() {
        return value;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;
        return new EqualsBuilder()
            .append(this.value, money.getValue())
            .append(this.currency, money.getCurrency())
            .isEquals();
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return value + " " + currency.getCurrencyCode(); 
    }
}
