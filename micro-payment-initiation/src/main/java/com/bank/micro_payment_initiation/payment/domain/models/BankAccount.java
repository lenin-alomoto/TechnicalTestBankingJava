package com.bank.micro_payment_initiation.payment.domain.models;

import java.util.Objects;

public class BankAccount {
	private String iban;

	public BankAccount() {
	}

	public BankAccount(String iban) {
		this.iban = iban;
	}

	public void validate() {
		if (iban == null || iban.isBlank()) {
			throw new IllegalArgumentException("iban is required");
		}
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	@Override
	public int hashCode() {
		return Objects.hash(iban);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof BankAccount))
			return false;
		BankAccount other = (BankAccount) obj;
		return Objects.equals(iban, other.iban);
	}
}
