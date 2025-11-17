package com.bank.micro_payment_initiation.payment.infrastructure.rest.handler;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
