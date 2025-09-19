package com.ecom.product.exception;

public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 3332091939806554003L;

	public NotFoundException(String message) {
		super(message);
	}
}
