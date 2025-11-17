package com.bank.micro_payment_initiation.payment.infrastructure.adapter.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.micro_payment_initiation.payment.infrastructure.adapter.entities.PaymentOrderEntity;

public interface PaymentOrderJpaRepository extends JpaRepository<PaymentOrderEntity, UUID> {

	Optional<PaymentOrderEntity> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);
}
