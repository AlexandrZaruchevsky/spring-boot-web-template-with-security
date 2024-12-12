package ru.az.sandbox.security.model.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class ZException extends Exception{

	private static final long serialVersionUID = 1759977173283183057L;
	
	@Getter
	private final HttpStatus status;
	@Getter
	private final Throwable parent;

	public ZException(HttpStatus status, String message, Throwable parent) {
		super(message);
		this.status = status;
		this.parent = parent;
	}
	
	public ZException(HttpStatus status, String message) {
		this(status, message, null);
	}
		
	public ZException(String message) {
		this(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
	}
	
}
