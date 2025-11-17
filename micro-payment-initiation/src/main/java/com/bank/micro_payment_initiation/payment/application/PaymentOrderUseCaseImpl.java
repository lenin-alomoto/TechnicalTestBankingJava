package com.bank.micro_payment_initiation.payment.application;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrderStatus;
import com.bank.micro_payment_initiation.payment.domain.port.PaymentOrderRepositoryPort;
import com.bank.micro_payment_initiation.payment.domain.usecase.PaymentOrderUseCase;

@Service
public class PaymentOrderUseCaseImpl implements PaymentOrderUseCase {

	private static final Logger log = LoggerFactory.getLogger(PaymentOrderUseCaseImpl.class);

	private final PaymentOrderRepositoryPort repositoryPort;

	public PaymentOrderUseCaseImpl(PaymentOrderRepositoryPort repositoryPort) {
		this.repositoryPort = repositoryPort;
	}

	@Override
	@Transactional
	public PaymentOrder initiatePayment(PaymentOrder paymentOrder) {
		log.info("Initiating payment for externalId={}", paymentOrder.getExternalId());

		// Validaciones de dominio
		paymentOrder.validateForInitiation();

		// Idempotencia básica por externalId
		if (repositoryPort.existsByExternalId(paymentOrder.getExternalId())) {
			return repositoryPort.findByExternalId(paymentOrder.getExternalId())
					.orElseThrow(() -> new IllegalStateException("Inconsistent state for externalId"));
		}

		// Generar ID interno si viene nulo
		if (paymentOrder.getPaymentOrderId() == null) {
			paymentOrder.setPaymentOrderId(UUID.randomUUID());
		}

		// Inicializar timestamps y estado
		paymentOrder.initializeTimestampsIfNeeded();
		paymentOrder.setStatus(PaymentOrderStatus.RECEIVED);

		PaymentOrder created = repositoryPort.save(paymentOrder);

		log.info("Payment order stored: {}", created.getPaymentOrderId());

		// Guardar
		return created;
	}

}
