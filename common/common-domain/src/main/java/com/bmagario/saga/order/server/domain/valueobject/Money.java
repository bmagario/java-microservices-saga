package com.bmagario.saga.order.server.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {

    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money money)) {
            return false;
        }
        return Objects.equals(getAmount(), money.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAmount());
    }

    private BigDecimal getScale(BigDecimal input) {
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }

    public boolean isGreaterThanZero() {
        return this.amount != null && this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money money) {
        return this.amount != null && this.amount.compareTo(money.getAmount()) > 0;
    }

    public Money add(Money money) {
        return new Money(getScale(this.amount.add(money.getAmount())));
    }

    public Money substract(Money money) {
        return new Money(getScale(this.amount.subtract(money.getAmount())));
    }

    public Money multiply(Integer multiplier) {
        return new Money(getScale(this.amount.multiply(new BigDecimal(multiplier))));
    }
}
