package com.bank.micro_payment_initiation.payment.infrastructure.adapter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.infrastructure.adapter.entities.PaymentOrderEntity;
import com.bank.micro_payment_initiation.payment.infrastructure.adapter.mappers.PaymentOrderPersistenceMapper;
import com.bank.micro_payment_initiation.payment.infrastructure.adapter.repositories.PaymentOrderJpaRepository;

class PaymentOrderRepositoryAdapterTest {

	private PaymentOrderJpaRepository jpaRepository;
	private PaymentOrderPersistenceMapper mapper;
	private PaymentOrderRepositoryAdapter adapter;

	@BeforeEach
	void setup() {
		jpaRepository = mock(PaymentOrderJpaRepository.class);
		mapper = mock(PaymentOrderPersistenceMapper.class);
		adapter = new PaymentOrderRepositoryAdapter(jpaRepository, mapper);
	}

	@Test
	void save_shouldMapAndPersistCorrectly() {
		PaymentOrder domain = new PaymentOrder();
		PaymentOrderEntity entity = new PaymentOrderEntity();
		PaymentOrderEntity savedEntity = new PaymentOrderEntity();
		PaymentOrder mappedBack = new PaymentOrder();

		when(mapper.toEntity(domain)).thenReturn(entity);
		when(jpaRepository.save(entity)).thenReturn(savedEntity);
		when(mapper.toDomain(savedEntity)).thenReturn(mappedBack);

		PaymentOrder result = adapter.save(domain);

		assertThat(result).isEqualTo(mappedBack);
	}

	@Test
	void findById_shouldReturnEmpty_whenInvalidUUID() {
		Optional<PaymentOrder> result = adapter.findById("INVALID");

		assertThat(result).isEmpty();
		verifyNoInteractions(jpaRepository);
	}

	@Test
	void findById_shouldMapEntityToDomain() {
		String id = UUID.randomUUID().toString();
		PaymentOrderEntity entity = new PaymentOrderEntity();
		PaymentOrder mapped = new PaymentOrder();

		when(jpaRepository.findById(UUID.fromString(id))).thenReturn(Optional.of(entity));
		when(mapper.toDomain(entity)).thenReturn(mapped);

		Optional<PaymentOrder> result = adapter.findById(id);

		assertThat(result).contains(mapped);
	}

	@Test
	void findByExternalId_shouldReturnMappedDomain() {
		PaymentOrderEntity entity = new PaymentOrderEntity();
		PaymentOrder mapped = new PaymentOrder();

		when(jpaRepository.findByExternalId("EXT")).thenReturn(Optional.of(entity));
		when(mapper.toDomain(entity)).thenReturn(mapped);

		Optional<PaymentOrder> result = adapter.findByExternalId("EXT");

		assertThat(result).contains(mapped);
	}

	@Test
	void existsByExternalId_shouldDelegateToRepository() {
		when(jpaRepository.existsByExternalId("ABC")).thenReturn(true);

		boolean exists = adapter.existsByExternalId("ABC");

		assertThat(exists).isTrue();
	}

}
