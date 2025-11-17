package com.bank.micro_payment_initiation.payment.infrastructure.adapter.mappers;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.bank.micro_payment_initiation.payment.domain.models.BankAccount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentAmount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrderStatus;
import com.bank.micro_payment_initiation.payment.infrastructure.adapter.entities.PaymentOrderEntity;

class PaymentOrderPersistenceMapperTest {

	private final PaymentOrderPersistenceMapper mapper = Mappers.getMapper(PaymentOrderPersistenceMapper.class);

	private PaymentOrder sampleDomain() {
		PaymentOrder order = new PaymentOrder();
		order.setPaymentOrderId(UUID.randomUUID());
		order.setExternalId("EXT-123");
		order.setDebtorAccount(new BankAccount("DEBTX"));
		order.setCreditorAccount(new BankAccount("CREDX"));
		order.setAmount(new PaymentAmount(new BigDecimal("10.50"), "USD"));
		order.setRemittanceInfo("Test remittance");
		order.setRequestedExecutionDate(LocalDate.now());
		order.setStatus(PaymentOrderStatus.ACCEPTED);
		return order;
	}

	@Test
	void toEntity_shouldMapDomainToEntityCorrectly() {
		PaymentOrder domain = sampleDomain();

		PaymentOrderEntity entity = mapper.toEntity(domain);

		assertThat(entity.getDebtorIban()).isEqualTo("DEBTX");
		assertThat(entity.getCreditorIban()).isEqualTo("CREDX");
		assertThat(entity.getAmount()).isEqualTo(new BigDecimal("10.50"));
		assertThat(entity.getCurrency()).isEqualTo("USD");
		assertThat(entity.getStatus()).isEqualTo("ACCEPTED");
	}

	@Test
	void toDomain_shouldMapEntityToDomainCorrectly() {
		PaymentOrderEntity e = new PaymentOrderEntity();
		e.setPaymentOrderId(UUID.randomUUID());
		e.setExternalId("EXT-100");
		e.setDebtorIban("IBAN-A");
		e.setCreditorIban("IBAN-B");
		e.setAmount(new BigDecimal("20.00"));
		e.setCurrency("EUR");
		e.setStatus("REJECTED");

		PaymentOrder domain = mapper.toDomain(e);

		assertThat(domain.getDebtorAccount().getIban()).isEqualTo("IBAN-A");
		assertThat(domain.getCreditorAccount().getIban()).isEqualTo("IBAN-B");
		assertThat(domain.getAmount().getAmount()).isEqualTo("20.00");
		assertThat(domain.getAmount().getCurrency()).isEqualTo("EUR");
		assertThat(domain.getStatus()).isEqualTo(PaymentOrderStatus.REJECTED);
	}
}
