package com.bank.micro_payment_initiation.payment.infrastructure.rest.mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderInitiateRequest;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderInitiateResponse;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderRetrieveResponse;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderStatusResponse;
import com.bank.micro_payment_initiation.payment.domain.models.BankAccount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentAmount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;

@Mapper(componentModel = "spring")
public interface PaymentOrderRestMapper {

    // ----------------------------
    // Helpers para evitar líneas largas
    // ----------------------------
    default OffsetDateTime mapLastUpdate(LocalDateTime ts) {
        return ts != null ? ts.atOffset(ZoneOffset.UTC) : null;
    }

    // ----------------------------
    // toDomain
    // ----------------------------
    @Mappings({
        @Mapping(target = "paymentOrderId", ignore = true),
        @Mapping(target = "status", ignore = true),
        @Mapping(target = "creationTimestamp", ignore = true),
        @Mapping(target = "lastUpdateTimestamp", ignore = true),

        @Mapping(target = "externalId", source = "externalReference"),

        @Mapping(target = "debtorAccount.iban", source = "debtorAccount.iban"),
        @Mapping(target = "creditorAccount.iban", source = "creditorAccount.iban"),

        @Mapping(target = "amount.amount", source = "instructedAmount.amount"),
        @Mapping(target = "amount.currency", source = "instructedAmount.currency"),

        @Mapping(target = "remittanceInfo", source = "remittanceInformation")
    })
    PaymentOrder toDomain(PaymentOrderInitiateRequest request);

    // ----------------------------
    // toInitiateResponse
    // ----------------------------
    @Mappings({
        @Mapping(target = "paymentOrderId",
                 expression = "java(paymentOrder.getPaymentOrderId().toString())"),

        @Mapping(target = "status",
                 expression = "java(paymentOrder.getStatus() != null ? paymentOrder.getStatus().name() : null)")
    })
    PaymentOrderInitiateResponse toInitiateResponse(PaymentOrder paymentOrder);

    // ----------------------------
    // toRetrieveResponse
    // ----------------------------
    @Mappings({
        @Mapping(target = "paymentOrderId",
                 expression = "java(paymentOrder.getPaymentOrderId().toString())"),

        @Mapping(target = "externalReference", source = "externalId"),

        @Mapping(target = "debtorAccount.iban", source = "debtorAccount.iban"),
        @Mapping(target = "creditorAccount.iban", source = "creditorAccount.iban"),

        @Mapping(target = "instructedAmount.amount", source = "amount.amount"),
        @Mapping(target = "instructedAmount.currency", source = "amount.currency"),

        @Mapping(target = "remittanceInformation", source = "remittanceInfo"),
        @Mapping(target = "requestedExecutionDate", source = "requestedExecutionDate"),

        @Mapping(target = "status",
                 expression = "java(paymentOrder.getStatus() != null ? paymentOrder.getStatus().name() : null)"),

        // ⭐ Línea corta usando helper
        @Mapping(target = "lastUpdate",
                 expression = "java(mapLastUpdate(paymentOrder.getLastUpdateTimestamp()))")
    })
    PaymentOrderRetrieveResponse toRetrieveResponse(PaymentOrder paymentOrder);

    // ----------------------------
    // toStatusResponse
    // ----------------------------
    @Mappings({
        @Mapping(target = "paymentOrderId",
                 expression = "java(paymentOrder.getPaymentOrderId().toString())"),

        @Mapping(target = "status",
                 expression = "java(paymentOrder.getStatus() != null ? paymentOrder.getStatus().name() : null)"),

        @Mapping(target = "lastUpdate",
                 expression = "java(mapLastUpdate(paymentOrder.getLastUpdateTimestamp()))")
    })
    PaymentOrderStatusResponse toStatusResponse(PaymentOrder paymentOrder);

    default BankAccount toBankAccount(String iban) {
        return new BankAccount(iban);
    }

    default PaymentAmount toPaymentAmount(BigDecimal amount, String currency) {
        return new PaymentAmount(amount, currency);
    }
}

