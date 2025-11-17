package com.bank.micro_payment_initiation.payment.domain.models;

import java.math.BigDecimal;
import java.util.Objects;

public class PaymentAmount {
	private BigDecimal amount;
    private String currency;

    public PaymentAmount() {
    }

    public PaymentAmount(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public void validate() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency is required");
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PaymentAmount)) return false;
        PaymentAmount other = (PaymentAmount) obj;
        return Objects.equals(amount, other.amount)
                && Objects.equals(currency, other.currency);
    }
}
