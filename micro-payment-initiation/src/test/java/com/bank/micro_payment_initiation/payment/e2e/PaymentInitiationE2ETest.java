package com.bank.micro_payment_initiation.payment.e2e;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderInitiateResponse;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderRetrieveResponse;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderStatusResponse;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentInitiationE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    private String baseUrl() {
        return "http://localhost:" + port + "/payment-initiation";
    }

    @Test
    void endToEndFlow_shouldCreateAndRetrievePaymentOrder() {

        //---------------------------------------------------
        // 1. POST → Crear Payment Order
        //---------------------------------------------------
        String body = """
        {
          "externalReference": "EXT-E2E-001",
          "debtorAccount": {"iban": "DEBTOR-E2E"},
          "creditorAccount": {"iban": "CREDITOR-E2E"},
          "instructedAmount": {"amount": 10.50, "currency": "USD"},
          "requestedExecutionDate": "2026-01-01"
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<PaymentOrderInitiateResponse> createdResp =
                rest.exchange(
                        baseUrl() + "/payment-orders",
                        HttpMethod.POST,
                        new HttpEntity<>(body, headers),
                        PaymentOrderInitiateResponse.class);

        assertThat(createdResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        PaymentOrderInitiateResponse created = createdResp.getBody();
        assertThat(created).isNotNull();

        String id = created.getPaymentOrderId();
        assertThat(id).isNotBlank();


        //---------------------------------------------------
        // 2. GET → Recuperar Payment Order
        //---------------------------------------------------
        ResponseEntity<PaymentOrderRetrieveResponse> retrieveResp =
                rest.getForEntity(
                        baseUrl() + "/payment-orders/" + id,
                        PaymentOrderRetrieveResponse.class);

        assertThat(retrieveResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        PaymentOrderRetrieveResponse retrieved = retrieveResp.getBody();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getPaymentOrderId()).isEqualTo(id);
        assertThat(retrieved.getInstructedAmount().getAmount())
                .isEqualByComparingTo(new BigDecimal("10.50"));


        //---------------------------------------------------
        // 3. GET → Status
        //---------------------------------------------------
        ResponseEntity<PaymentOrderStatusResponse> statusResp =
                rest.getForEntity(
                        baseUrl() + "/payment-orders/" + id + "/status",
                        PaymentOrderStatusResponse.class);

        assertThat(statusResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        PaymentOrderStatusResponse status = statusResp.getBody();
        assertThat(status).isNotNull();
        assertThat(status.getStatus()).isEqualTo("RECEIVED");
    }
}
