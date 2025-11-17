package com.bank.micro_payment_initiation.payment.domain.usecase;

import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;

public interface PaymentOrderUseCase {
	PaymentOrder initiatePayment(PaymentOrder paymentOrder);
}
