package com.bank.micro_payment_initiation.payment.infrastructure.rest.handler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.micro_payment_initiation.payment.domain.usecase.PaymentOrderUseCase;
import com.bank.micro_payment_initiation.payment.domain.usecase.RetrievePaymentUseCase;
import com.bank.micro_payment_initiation.payment.infrastructure.rest.controllers.PaymentOrdersController;
import com.bank.micro_payment_initiation.payment.infrastructure.rest.mappers.PaymentOrderRestMapper;

@WebMvcTest(PaymentOrdersController.class)
@Import(GlobalErrorHandler.class)
class GlobalErrorHandlerTest {

	@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentOrderUseCase paymentOrderUseCase;

    @MockitoBean
    private RetrievePaymentUseCase retrievePaymentUseCase;

    @MockitoBean
    private PaymentOrderRestMapper restMapper;

    // ----------------------------------------------------
    // 404 - RESOURCE NOT FOUND
    // ----------------------------------------------------
    @Test
    void shouldReturn404_whenResourceNotFound() throws Exception {

        when(retrievePaymentUseCase.retrievePayment(anyString()))
                .thenThrow(new ResourceNotFoundException("Payment order not found"));

        mockMvc.perform(get("/payment-initiation/payment-orders/123"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Payment order not found"))
                .andExpect(jsonPath("$.instance").value("/payment-initiation/payment-orders/123"));
    }

    // ----------------------------------------------------
    // 400 - BAD REQUEST (IllegalArgumentException)
    // ----------------------------------------------------
    @Test
    void shouldReturn400_whenBadRequest() throws Exception {

        when(retrievePaymentUseCase.retrievePayment(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid ID format"));

        mockMvc.perform(get("/payment-initiation/payment-orders/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid ID format"));
    }

    // ----------------------------------------------------
    // 500 - UNEXPECTED ERROR
    // ----------------------------------------------------
    @Test
    void shouldReturn500_whenUnexpectedError() throws Exception {

        when(retrievePaymentUseCase.retrievePayment(anyString()))
                .thenThrow(new RuntimeException("Boom!"));

        mockMvc.perform(get("/payment-initiation/payment-orders/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("Boom!"));
    }
}
