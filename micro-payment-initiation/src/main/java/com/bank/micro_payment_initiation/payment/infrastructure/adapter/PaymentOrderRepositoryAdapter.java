package com.bank.micro_payment_initiation.payment.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.port.PaymentOrderRepositoryPort;
import com.bank.micro_payment_initiation.payment.infrastructure.adapter.entities.PaymentOrderEntity;
import com.bank.micro_payment_initiation.payment.infrastructure.adapter.mappers.PaymentOrderPersistenceMapper;
import com.bank.micro_payment_initiation.payment.infrastructure.adapter.repositories.PaymentOrderJpaRepository;

@Repository
public class PaymentOrderRepositoryAdapter implements PaymentOrderRepositoryPort {

	private static final Logger log = LoggerFactory.getLogger(PaymentOrderRepositoryAdapter.class);

	private final PaymentOrderJpaRepository jpaRepository;
	private final PaymentOrderPersistenceMapper mapper;

	public PaymentOrderRepositoryAdapter(PaymentOrderJpaRepository jpaRepository,
			PaymentOrderPersistenceMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public PaymentOrder save(PaymentOrder paymentOrder) {
		log.debug("Saving PaymentOrder externalId={}", paymentOrder.getExternalId());
		PaymentOrderEntity entity = mapper.toEntity(paymentOrder);
		PaymentOrderEntity saved = jpaRepository.save(entity);
		log.debug("Saved PaymentOrder id={}", saved.getPaymentOrderId());
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<PaymentOrder> findById(String paymentOrderId) {
		UUID id;
		try {
			id = UUID.fromString(paymentOrderId);
			log.debug("Searching PaymentOrder id={}", id);
		} catch (IllegalArgumentException ex) {
			return Optional.empty();
		}
		return jpaRepository.findById(id).map(mapper::toDomain);
	}

	@Override
	public Optional<PaymentOrder> findByExternalId(String externalId) {
		log.debug("Searching externalId id={}", externalId);
		return jpaRepository.findByExternalId(externalId).map(mapper::toDomain);
	}

	@Override
	public boolean existsByExternalId(String externalId) {
		log.debug("Searching exist external id={}", externalId);
		return jpaRepository.existsByExternalId(externalId);
	}

}
