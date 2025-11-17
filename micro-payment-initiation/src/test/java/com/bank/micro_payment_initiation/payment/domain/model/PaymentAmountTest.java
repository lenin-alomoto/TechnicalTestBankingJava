package com.bank.micro_payment_initiation.payment.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bank.micro_payment_initiation.payment.domain.models.PaymentAmount;

class PaymentAmountTest {

	@Test
	void validate_shouldThrow_whenAmountInvalid() {
		PaymentAmount a1 = new PaymentAmount(null, "USD");
		PaymentAmount a2 = new PaymentAmount(BigDecimal.ZERO, "USD");
		PaymentAmount a3 = new PaymentAmount(new BigDecimal("-1"), "USD");

		assertThatThrownBy(a1::validate).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(a2::validate).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(a3::validate).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void validate_shouldThrow_whenCurrencyInvalid() {
		PaymentAmount a1 = new PaymentAmount(new BigDecimal("10"), null);
		PaymentAmount a2 = new PaymentAmount(new BigDecimal("10"), "  ");

		assertThatThrownBy(a1::validate).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(a2::validate).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void validate_shouldPass_whenValid() {
		PaymentAmount pa = new PaymentAmount(new BigDecimal("10"), "USD");
		pa.validate();
		assertThat(pa.getCurrency()).isEqualTo("USD");
	}

	@Test
	void equalsAndHashcode_shouldWork() {
		PaymentAmount a1 = new PaymentAmount(new BigDecimal("10"), "USD");
		PaymentAmount a2 = new PaymentAmount(new BigDecimal("10"), "USD");

		assertThat(a1).isEqualTo(a2);
		assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
	}

}
