package com.bank.micro_payment_initiation.payment.domain.port;

import java.util.Optional;

import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;

public interface PaymentOrderRepositoryPort {
	
	PaymentOrder save(PaymentOrder paymentOrder);
	
    Optional<PaymentOrder> findById(String paymentOrderId);
    
    Optional<PaymentOrder> findByExternalId(String externalId);
    
    boolean existsByExternalId(String externalId);
}
