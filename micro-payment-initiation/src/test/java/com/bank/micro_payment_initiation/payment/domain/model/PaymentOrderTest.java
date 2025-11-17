package com.bank.micro_payment_initiation.payment.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.bank.micro_payment_initiation.payment.domain.models.BankAccount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentAmount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrderStatus;

class PaymentOrderTest {

	private PaymentOrder validOrder() {
		PaymentOrder order = new PaymentOrder();
		order.setExternalId("EXT123");
		order.setDebtorAccount(new BankAccount("DEBTOR"));
		order.setCreditorAccount(new BankAccount("CREDITOR"));
		order.setAmount(new PaymentAmount(new BigDecimal("10"), "USD"));
		order.setRequestedExecutionDate(LocalDate.now());
		return order;
	}

	@Test
	void validateForInitiation_shouldThrow_forMissingFields() {
		PaymentOrder o = validOrder();
		o.setExternalId(null);

		assertThatThrownBy(o::validateForInitiation).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("externalId");
	}

	@Test
	void validateForInitiation_shouldPass_whenValid() {
		PaymentOrder o = validOrder();
		o.validateForInitiation();
	}

	@Test
	void initializeTimestampsIfNeeded_shouldSetBoth_whenNull() {
		PaymentOrder o = validOrder();
		o.initializeTimestampsIfNeeded();

		assertThat(o.getCreationTimestamp()).isNotNull();
		assertThat(o.getLastUpdateTimestamp()).isNotNull();
	}

	@Test
	void initializeTimestampsIfNeeded_shouldNotOverride_existingValues() {
		PaymentOrder o = validOrder();
		LocalDateTime past = LocalDateTime.now().minusDays(1);

		o.setCreationTimestamp(past);
		o.setLastUpdateTimestamp(past);

		o.initializeTimestampsIfNeeded();

		assertThat(o.getCreationTimestamp()).isEqualTo(past);
		assertThat(o.getLastUpdateTimestamp()).isEqualTo(past);
	}

	@Test
	void statusTransitions_shouldUpdateStatus() {
		PaymentOrder o = validOrder();

		o.markAsReceived();
		assertThat(o.getStatus()).isEqualTo(PaymentOrderStatus.RECEIVED);

		o.markAsAccepted();
		assertThat(o.getStatus()).isEqualTo(PaymentOrderStatus.ACCEPTED);

		o.markAsExecuted();
		assertThat(o.getStatus()).isEqualTo(PaymentOrderStatus.EXECUTED);

		o.markAsRejected();
		assertThat(o.getStatus()).isEqualTo(PaymentOrderStatus.REJECTED);
	}

	@Test
	void equals_shouldMatchByPaymentOrderId() {
		UUID id = UUID.randomUUID();

		PaymentOrder o1 = new PaymentOrder();
		o1.setPaymentOrderId(id);

		PaymentOrder o2 = new PaymentOrder();
		o2.setPaymentOrderId(id);

		assertThat(o1).isEqualTo(o2);
		assertThat(o1.hashCode()).isEqualTo(o2.hashCode());
	}

	@Test
	void equals_shouldFail_whenDifferentId() {
		PaymentOrder o1 = new PaymentOrder();
		o1.setPaymentOrderId(UUID.randomUUID());

		PaymentOrder o2 = new PaymentOrder();
		o2.setPaymentOrderId(UUID.randomUUID());

		assertThat(o1).isNotEqualTo(o2);
	}

}
