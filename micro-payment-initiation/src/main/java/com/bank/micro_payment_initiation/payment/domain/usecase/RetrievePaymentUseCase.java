package com.bank.micro_payment_initiation.payment.domain.usecase;

import java.util.Optional;

import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;

public interface RetrievePaymentUseCase {
	Optional<PaymentOrder> retrievePayment(String paymentOrderId);
    Optional<PaymentOrder> retrievePaymentByExternalId(String externalId);
}
