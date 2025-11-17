package com.bank.micro_payment_initiation.payment.application;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.port.PaymentOrderRepositoryPort;
import com.bank.micro_payment_initiation.payment.domain.usecase.RetrievePaymentUseCase;

@Service
public class RetrievePaymentUseCaseImpl implements RetrievePaymentUseCase {

	private static final Logger log = LoggerFactory.getLogger(RetrievePaymentUseCaseImpl.class);

	private final PaymentOrderRepositoryPort repositoryPort;

	public RetrievePaymentUseCaseImpl(PaymentOrderRepositoryPort repositoryPort) {
		this.repositoryPort = repositoryPort;
	}

	@Override
	public Optional<PaymentOrder> retrievePayment(String paymentOrderId) {
		log.debug("Looking for paymentOrder {}", paymentOrderId);

		return repositoryPort.findById(paymentOrderId);
	}

	@Override
	public Optional<PaymentOrder> retrievePaymentByExternalId(String externalId) {
		log.debug("Looking for externalId {}", externalId);

		return repositoryPort.findByExternalId(externalId);
	}

}
