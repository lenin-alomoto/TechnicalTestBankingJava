package com.bank.micro_payment_initiation.payment.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bank.micro_payment_initiation.payment.domain.models.BankAccount;

class BankAccountTest {

	@Test
	void validate_shouldThrow_whenIbanIsNullOrBlank() {
		BankAccount acc1 = new BankAccount(null);
		BankAccount acc2 = new BankAccount("  ");

		assertThatThrownBy(acc1::validate).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("iban is required");

		assertThatThrownBy(acc2::validate).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("iban is required");
	}

	@Test
	void validate_shouldPass_whenValidIban() {
		BankAccount acc = new BankAccount("EC123");
		acc.validate();
		assertThat(acc.getIban()).isEqualTo("EC123");
	}

	@Test
	void equalsAndHashcode_shouldWork() {
		BankAccount a1 = new BankAccount("A1");
		BankAccount a2 = new BankAccount("A1");
		assertThat(a1).isEqualTo(a2);
		assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
	}

}
