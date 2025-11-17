package com.bank.micro_payment_initiation.payment.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.bank.micro_payment_initiation.payment.domain.models.BankAccount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentAmount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrderStatus;
import com.bank.micro_payment_initiation.payment.domain.port.PaymentOrderRepositoryPort;

class PaymentOrderUseCaseImplTest {

	private PaymentOrderRepositoryPort repositoryPort;
	private PaymentOrderUseCaseImpl useCase;

	@BeforeEach
	void setup() {
		repositoryPort = mock(PaymentOrderRepositoryPort.class);
		useCase = new PaymentOrderUseCaseImpl(repositoryPort);
	}

	private PaymentOrder validOrder() {
		PaymentOrder o = new PaymentOrder();
		o.setExternalId("EXT-100");
		o.setDebtorAccount(new BankAccount("DEBTOR123"));
		o.setCreditorAccount(new BankAccount("CREDITOR123"));
		o.setAmount(new PaymentAmount(new BigDecimal("10.00"), "USD"));
		o.setRequestedExecutionDate(LocalDate.now());
		return o;
	}

	@Test
	void initiatePayment_shouldThrow_whenInvalid() {
		PaymentOrder o = validOrder();
		o.setExternalId(null);

		assertThatThrownBy(() -> useCase.initiatePayment(o)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void initiatePayment_shouldReturnExisting_whenExternalIdAlreadyExists() {
		PaymentOrder existing = validOrder();
		existing.setPaymentOrderId(UUID.randomUUID());

		when(repositoryPort.existsByExternalId("EXT-100")).thenReturn(true);
		when(repositoryPort.findByExternalId("EXT-100")).thenReturn(Optional.of(existing));

		PaymentOrder result = useCase.initiatePayment(validOrder());

		assertThat(result).isEqualTo(existing);
		verify(repositoryPort, never()).save(any());
	}

	@Test
	void initiatePayment_shouldCreateNewPaymentOrder_whenValid() {
		PaymentOrder order = validOrder();

		when(repositoryPort.existsByExternalId("EXT-100")).thenReturn(false);
		when(repositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

		PaymentOrder result = useCase.initiatePayment(order);

		// Verifica UUID asignado
		assertThat(result.getPaymentOrderId()).isNotNull();

		// Verifica timestamps
		assertThat(result.getCreationTimestamp()).isNotNull();
		assertThat(result.getLastUpdateTimestamp()).isNotNull();

		// Verifica estado inicial
		assertThat(result.getStatus()).isEqualTo(PaymentOrderStatus.RECEIVED);

		// Captura la entidad enviada al repo
		ArgumentCaptor<PaymentOrder> captor = ArgumentCaptor.forClass(PaymentOrder.class);
		verify(repositoryPort).save(captor.capture());

		PaymentOrder saved = captor.getValue();

		assertThat(saved.getExternalId()).isEqualTo("EXT-100");
	}

	@Test
	void initiatePayment_shouldNotOverrideExistingPaymentOrderId() {
		PaymentOrder order = validOrder();
		UUID existingId = UUID.randomUUID();
		order.setPaymentOrderId(existingId);

		when(repositoryPort.existsByExternalId("EXT-100")).thenReturn(false);
		when(repositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

		PaymentOrder result = useCase.initiatePayment(order);

		assertThat(result.getPaymentOrderId()).isEqualTo(existingId);
	}

}
