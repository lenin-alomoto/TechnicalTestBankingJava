package com.bank.micro_payment_initiation.payment.infrastructure.adapter.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.bank.micro_payment_initiation.payment.domain.models.BankAccount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentAmount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrderStatus;
import com.bank.micro_payment_initiation.payment.infrastructure.adapter.entities.PaymentOrderEntity;

@Mapper(componentModel = "spring")
public interface PaymentOrderPersistenceMapper {

	@Mapping(target = "debtorIban", source = "debtorAccount.iban")
	@Mapping(target = "creditorIban", source = "creditorAccount.iban")
	@Mapping(target = "amount", source = "amount.amount")
	@Mapping(target = "currency", source = "amount.currency")
	@Mapping(target = "status", source = "status")
	PaymentOrderEntity toEntity(PaymentOrder domain);

	@Mappings({ @Mapping(target = "debtorAccount.iban", source = "debtorIban"),
			@Mapping(target = "creditorAccount.iban", source = "creditorIban"),
			@Mapping(target = "amount.amount", source = "amount"),
			@Mapping(target = "amount.currency", source = "currency"), 
			@Mapping(target = "status", source = "status") 
	})
	PaymentOrder toDomain(PaymentOrderEntity entity);

	// ====== Métodos auxiliares para MapStruct ======

	default String map(PaymentOrderStatus status) {
		return status != null ? status.name() : null;
	}

	default PaymentOrderStatus map(String status) {
		return status != null ? PaymentOrderStatus.valueOf(status) : null;
	}

	default BankAccount toBankAccount(String iban) {
		return iban != null ? new BankAccount(iban) : null;
	}

	default PaymentAmount toPaymentAmount(java.math.BigDecimal amount, String currency) {
		return amount != null || currency != null ? new PaymentAmount(amount, currency) : null;
	}

}
