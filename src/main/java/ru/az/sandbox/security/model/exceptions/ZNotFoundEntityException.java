package ru.az.sandbox.security.model.exceptions;

import org.springframework.http.HttpStatus;

public class ZNotFoundEntityException extends ZException {

	private static final long serialVersionUID = 3213810460465501001L;
	
	public ZNotFoundEntityException(String message, Throwable parent) {
		super(HttpStatus.NOT_FOUND, message, parent);
	}

	public ZNotFoundEntityException(String message) {
		super(HttpStatus.NOT_FOUND, message, null);
	}

}
