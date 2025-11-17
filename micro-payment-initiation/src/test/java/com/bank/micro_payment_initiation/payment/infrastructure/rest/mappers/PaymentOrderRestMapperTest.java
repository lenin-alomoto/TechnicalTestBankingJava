package com.bank.micro_payment_initiation.payment.infrastructure.rest.mappers;


import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.bank.micro_payment_initiation.payment.api.model.AccountReference;
import com.bank.micro_payment_initiation.payment.api.model.Amount;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderInitiateRequest;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderInitiateResponse;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderRetrieveResponse;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderStatusResponse;
import com.bank.micro_payment_initiation.payment.domain.models.BankAccount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentAmount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrderStatus;

class PaymentOrderRestMapperTest {

	private final PaymentOrderRestMapper mapper = Mappers.getMapper(PaymentOrderRestMapper.class);

	private PaymentOrder sampleDomain() {
	    PaymentOrder p = new PaymentOrder();
	    p.setPaymentOrderId(UUID.randomUUID());
	    p.setExternalId("EXT123");
	    p.setDebtorAccount(new BankAccount("DEBTOR-IBAN"));
	    p.setCreditorAccount(new BankAccount("CREDITOR-IBAN"));
	    p.setAmount(new PaymentAmount(new BigDecimal("50.25"), "USD"));
	    p.setRequestedExecutionDate(LocalDate.of(2025, 11, 20));
	    p.setRemittanceInfo("TEST REM");
	    p.setStatus(PaymentOrderStatus.RECEIVED);

	    p.setCreationTimestamp(LocalDateTime.now());
	    p.setLastUpdateTimestamp(LocalDateTime.now());

	    return p;
	}

    // -------------------------------------------------------
    // TEST 1 — toDomain()
    // -------------------------------------------------------
    @Test
    void toDomain_shouldMapPaymentOrderInitiateRequestToDomain() {

        PaymentOrderInitiateRequest req = new PaymentOrderInitiateRequest()
                .externalReference("EXT999")
                .debtorAccount(new AccountReference().iban("DEBTOR-IBAN"))
                .creditorAccount(new AccountReference().iban("CREDITOR-IBAN"))
                .instructedAmount(new Amount().amount(new BigDecimal("99.99")).currency("EUR"))
                .requestedExecutionDate(LocalDate.of(2026, 1, 10))
                .remittanceInformation("Hello World");

        PaymentOrder result = mapper.toDomain(req);

        assertThat(result).isNotNull();
        assertThat(result.getExternalId()).isEqualTo("EXT999");
        assertThat(result.getDebtorAccount().getIban()).isEqualTo("DEBTOR-IBAN");
        assertThat(result.getCreditorAccount().getIban()).isEqualTo("CREDITOR-IBAN");

        assertThat(result.getAmount().getAmount()).isEqualByComparingTo("99.99");
        assertThat(result.getAmount().getCurrency()).isEqualTo("EUR");

        assertThat(result.getRemittanceInfo()).isEqualTo("Hello World");
        assertThat(result.getRequestedExecutionDate()).isEqualTo(LocalDate.of(2026, 1, 10));

        // campos ignorados en mapper:
        assertThat(result.getPaymentOrderId()).isNull();
        assertThat(result.getStatus()).isNull();
        assertThat(result.getCreationTimestamp()).isNull();
        assertThat(result.getLastUpdateTimestamp()).isNull();
    }

    // -------------------------------------------------------
    // TEST 2 — toInitiateResponse()
    // -------------------------------------------------------
    @Test
    void toInitiateResponse_shouldMapDomainToResponse() {

        PaymentOrder domain = sampleDomain();

        PaymentOrderInitiateResponse res = mapper.toInitiateResponse(domain);

        assertThat(res).isNotNull();
        assertThat(res.getPaymentOrderId()).isEqualTo(domain.getPaymentOrderId().toString());
        assertThat(res.getStatus()).isEqualTo(domain.getStatus().name());
    }

    // -------------------------------------------------------
    // TEST 3 — toRetrieveResponse()
    // -------------------------------------------------------
    @Test
    void toRetrieveResponse_shouldMapDomainToRetrieveResponse() {

        PaymentOrder domain = sampleDomain();

        PaymentOrderRetrieveResponse res = mapper.toRetrieveResponse(domain);

        assertThat(res).isNotNull();
        assertThat(res.getPaymentOrderId()).isEqualTo(domain.getPaymentOrderId().toString());
        assertThat(res.getExternalReference()).isEqualTo("EXT123");

        assertThat(res.getDebtorAccount().getIban()).isEqualTo("DEBTOR-IBAN");
        assertThat(res.getCreditorAccount().getIban()).isEqualTo("CREDITOR-IBAN");

        assertThat(res.getInstructedAmount().getAmount()).isEqualByComparingTo("50.25");
        assertThat(res.getInstructedAmount().getCurrency()).isEqualTo("USD");

        assertThat(res.getRequestedExecutionDate()).isEqualTo(LocalDate.of(2025, 11, 20));
        assertThat(res.getRemittanceInformation()).isEqualTo("TEST REM");
        assertThat(res.getStatus()).isEqualTo(domain.getStatus().name());
    }

    // -------------------------------------------------------
    // TEST 4 — toStatusResponse()
    // -------------------------------------------------------
    @Test
    void toStatusResponse_shouldMapDomainToStatusResponse() {

        PaymentOrder domain = sampleDomain();

        PaymentOrderStatusResponse res = mapper.toStatusResponse(domain);

        assertThat(res).isNotNull();
        assertThat(res.getPaymentOrderId()).isEqualTo(domain.getPaymentOrderId().toString());
        assertThat(res.getStatus()).isEqualTo(domain.getStatus().name());
    }

}
