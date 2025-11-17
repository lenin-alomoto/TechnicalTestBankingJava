package com.bank.micro_payment_initiation.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Payment Order Service - BIAN")
						.description("Servicio de órdenes de pago")
						.version("1.0.0")
						.contact(new io.swagger.v3.oas.models.info.Contact()
								.name("Lenin Alomoto")
								.email("lenin.alomoto@hotmail.com")));
	}

	@Bean
	GroupedOpenApi paymentOrderApi() {
		return GroupedOpenApi.builder().group("payment-order").pathsToMatch("/v1/payment-order/**").build();
	}
}