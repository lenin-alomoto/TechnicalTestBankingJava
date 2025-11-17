package com.bank.micro_payment_initiation.payment.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.port.PaymentOrderRepositoryPort;

class RetrievePaymentUseCaseImplTest {

	private PaymentOrderRepositoryPort repositoryPort;
	private RetrievePaymentUseCaseImpl useCase;

	@BeforeEach
	void setup() {
		repositoryPort = mock(PaymentOrderRepositoryPort.class);
		useCase = new RetrievePaymentUseCaseImpl(repositoryPort);
	}

	@Test
	void retrievePayment_shouldReturnOrder_whenExists() {
		PaymentOrder order = new PaymentOrder();
		when(repositoryPort.findById("123")).thenReturn(Optional.of(order));

		Optional<PaymentOrder> result = useCase.retrievePayment("123");

		assertThat(result).contains(order);
	}

	@Test
	void retrievePayment_shouldReturnEmpty_whenNotExists() {
		when(repositoryPort.findById("123")).thenReturn(Optional.empty());

		Optional<PaymentOrder> result = useCase.retrievePayment("123");

		assertThat(result).isEmpty();
	}

	@Test
	void retrievePaymentByExternalId_shouldReturnOrder_whenExists() {
		PaymentOrder order = new PaymentOrder();
		when(repositoryPort.findByExternalId("EXT-100")).thenReturn(Optional.of(order));

		Optional<PaymentOrder> result = useCase.retrievePaymentByExternalId("EXT-100");

		assertThat(result).contains(order);
	}

	@Test
	void retrievePaymentByExternalId_shouldReturnEmpty_whenNotExists() {
		when(repositoryPort.findByExternalId("EXT-100")).thenReturn(Optional.empty());

		Optional<PaymentOrder> result = useCase.retrievePaymentByExternalId("EXT-100");

		assertThat(result).isEmpty();
	}

}
