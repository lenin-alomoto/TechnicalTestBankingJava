package com.bank.micro_payment_initiation.payment.infrastructure.rest.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bank.micro_payment_initiation.payment.api.PaymentInitiationPaymentOrderApi;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderInitiateRequest;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderInitiateResponse;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderRetrieveResponse;
import com.bank.micro_payment_initiation.payment.api.model.PaymentOrderStatusResponse;
import com.bank.micro_payment_initiation.payment.domain.models.PaymentOrder;
import com.bank.micro_payment_initiation.payment.domain.usecase.PaymentOrderUseCase;
import com.bank.micro_payment_initiation.payment.domain.usecase.RetrievePaymentUseCase;
import com.bank.micro_payment_initiation.payment.infrastructure.rest.mappers.PaymentOrderRestMapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "PaymentOrder", description = "Operaciones de gestión de órdenes de pago")
public class PaymentOrdersController implements PaymentInitiationPaymentOrderApi {

	private static final Logger log = LoggerFactory.getLogger(PaymentOrdersController.class);

	private final PaymentOrderUseCase paymentOrderUseCase;
	private final RetrievePaymentUseCase retrievePaymentUseCase;
	private final PaymentOrderRestMapper restMapper;

	public PaymentOrdersController(PaymentOrderUseCase paymentOrderUseCase,
			RetrievePaymentUseCase retrievePaymentUseCase, PaymentOrderRestMapper restMapper) {
		this.paymentOrderUseCase = paymentOrderUseCase;
		this.retrievePaymentUseCase = retrievePaymentUseCase;
		this.restMapper = restMapper;
	}

	@Override
	public ResponseEntity<PaymentOrderInitiateResponse> initiatePaymentOrder(@Valid @RequestBody PaymentOrderInitiateRequest request) {
		PaymentOrder domain = restMapper.toDomain(request);
		PaymentOrder created = paymentOrderUseCase.initiatePayment(domain);
		PaymentOrderInitiateResponse response = restMapper.toInitiateResponse(created);

		log.info("PaymentOrder created successfully: {}", created.getPaymentOrderId());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Override
	public ResponseEntity<PaymentOrderRetrieveResponse> retrievePaymentOrder(String paymentOrderId) {
		Optional<PaymentOrder> result = retrievePaymentUseCase.retrievePayment(paymentOrderId);

		log.info("Retrieving payment order {}", paymentOrderId);

		return result.map(order -> ResponseEntity.ok(restMapper.toRetrieveResponse(order)))
				.orElse(ResponseEntity.notFound().build());
	}

	@Override
	public ResponseEntity<PaymentOrderStatusResponse> retrievePaymentOrderStatus(String paymentOrderId) {
		Optional<PaymentOrder> result = retrievePaymentUseCase.retrievePayment(paymentOrderId);

		log.info("Retrieving payment order status {}", paymentOrderId);

		return result.map(order -> ResponseEntity.ok(restMapper.toStatusResponse(order)))
				.orElse(ResponseEntity.notFound().build());
	}
}
