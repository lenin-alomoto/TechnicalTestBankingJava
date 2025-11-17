package com.bank.micro_payment_initiation.payment.infrastructure.rest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderInitiateRequest;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderInitiateResponse;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderRetrieveResponse;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderStatusResponse;
import com.bank.micro_payment_initiation.payment.domain.models.BankAccount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentAmount;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrderStatus;
import com.bank.micro_payment_initiation.payment.domain.usecase.PaymentOrderUseCase;
import com.bank.micro_payment_initiation.payment.domain.usecase.RetrievePaymentUseCase;
import com.bank.micro_payment_initiation.payment.infrastructure.rest.mappers.PaymentOrderRestMapper;

@WebMvcTest(PaymentOrdersController.class)
class PaymentOrdersControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PaymentOrderUseCase paymentOrderUseCase;

	@MockitoBean
	private RetrievePaymentUseCase retrievePaymentUseCase;

	@MockitoBean
	private PaymentOrderRestMapper restMapper;

	private PaymentOrder sampleOrder() {
		PaymentOrder order = new PaymentOrder();
		order.setPaymentOrderId(UUID.randomUUID());
		order.setExternalId("EXT123");
		order.setDebtorAccount(new BankAccount("DEBTOR"));
		order.setCreditorAccount(new BankAccount("CREDITOR"));
		order.setAmount(new PaymentAmount(new BigDecimal("10.00"), "USD"));
		order.setRequestedExecutionDate(LocalDate.now());
		order.setStatus(PaymentOrderStatus.RECEIVED);
		return order;
	}

	// -----------------------------
	// POST /payment-orders
	// -----------------------------
	@Test
	void initiatePaymentOrder_shouldReturn201() throws Exception {

		PaymentOrder domainBefore = sampleOrder();
		PaymentOrder domainAfter = sampleOrder();

		PaymentOrderInitiateResponse response = new PaymentOrderInitiateResponse()
				.paymentOrderId(domainAfter.getPaymentOrderId().toString()).status(domainAfter.getStatus().name());

		when(restMapper.toDomain(any(PaymentOrderInitiateRequest.class))).thenReturn(domainBefore);
		when(paymentOrderUseCase.initiatePayment(domainBefore)).thenReturn(domainAfter);
		when(restMapper.toInitiateResponse(domainAfter)).thenReturn(response);

		mockMvc.perform(post("/payment-initiation/payment-orders").contentType(MediaType.APPLICATION_JSON).content("""
				{
				  "externalReference": "EXT123",
				  "debtorAccount": {"iban": "DEBTOR"},
				  "creditorAccount": {"iban": "CREDITOR"},
				  "instructedAmount": {"amount": 10, "currency": "USD"},
				  "requestedExecutionDate": "2025-11-16"
				}
				""")).andExpect(status().isCreated())
				.andExpect(jsonPath("$.paymentOrderId").value(domainAfter.getPaymentOrderId().toString()))
				.andExpect(jsonPath("$.status").value(domainAfter.getStatus().name()));
	}

	// -----------------------------
	// GET /payment-orders/{id}
	// -----------------------------
	@Test
	void retrievePaymentOrder_shouldReturn200() throws Exception {
		PaymentOrder order = sampleOrder();
		PaymentOrderRetrieveResponse response = new PaymentOrderRetrieveResponse()
				.paymentOrderId(order.getPaymentOrderId().toString()).externalReference(order.getExternalId())
				.status(order.getStatus().name());

		when(retrievePaymentUseCase.retrievePayment(order.getPaymentOrderId().toString()))
				.thenReturn(Optional.of(order));

		when(restMapper.toRetrieveResponse(order)).thenReturn(response);

		mockMvc.perform(get("/payment-initiation/payment-orders/" + order.getPaymentOrderId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.paymentOrderId").value(order.getPaymentOrderId().toString()))
				.andExpect(jsonPath("$.status").value(order.getStatus().name()));
	}

	@Test
	void retrievePaymentOrder_shouldReturn404_whenNotFound() throws Exception {
		when(retrievePaymentUseCase.retrievePayment("ABC")).thenReturn(Optional.empty());

		mockMvc.perform(get("/payment-initiation/payment-orders/ABC")).andExpect(status().isNotFound());
	}

	// -----------------------------
	// GET /payment-orders/{id}/status
	// -----------------------------
	@Test
	void retrievePaymentOrderStatus_shouldReturn200() throws Exception {
		PaymentOrder order = sampleOrder();
		PaymentOrderStatusResponse response = new PaymentOrderStatusResponse()
				.paymentOrderId(order.getPaymentOrderId().toString()).status(order.getStatus().name());

		when(retrievePaymentUseCase.retrievePayment(order.getPaymentOrderId().toString()))
				.thenReturn(Optional.of(order));

		when(restMapper.toStatusResponse(order)).thenReturn(response);

		mockMvc.perform(get("/payment-initiation/payment-orders/" + order.getPaymentOrderId() + "/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.paymentOrderId").value(order.getPaymentOrderId().toString()))
				.andExpect(jsonPath("$.status").value(order.getStatus().name()));
	}

	@Test
	void retrievePaymentOrderStatus_shouldReturn404_whenNotFound() throws Exception {
		when(retrievePaymentUseCase.retrievePayment("XYZ")).thenReturn(Optional.empty());

		mockMvc.perform(get("/payment-initiation/payment-orders/XYZ/status")).andExpect(status().isNotFound());
	}

	@Test
	void initiatePaymentOrder_shouldReturn400_whenValidationFails() throws Exception {

		when(restMapper.toDomain(any())).thenThrow(new IllegalArgumentException("externalId required"));

		mockMvc.perform(
				post("/payment-initiation/payment-orders").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void initiatePaymentOrder_shouldReturn500_whenUnexpectedException() throws Exception {

	    when(restMapper.toDomain(any())).thenThrow(new RuntimeException("boom"));

	    mockMvc.perform(post("/payment-initiation/payment-orders")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content("""
	            {
	              "externalReference": "EXT123",
	              "debtorAccount": {"iban": "DEBTOR"},
	              "creditorAccount": {"iban": "CREDITOR"},
	              "instructedAmount": {"amount": 10, "currency": "USD"},
	              "requestedExecutionDate": "2025-11-16"
	            }
	            """))
	            .andExpect(status().isInternalServerError());
	}


}
