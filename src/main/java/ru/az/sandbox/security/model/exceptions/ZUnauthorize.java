package ru.az.sandbox.security.model.exceptions;

import org.springframework.http.HttpStatus;

import io.jsonwebtoken.io.IOException;
import lombok.Getter;

public class ZUnauthorize extends IOException{

	private static final long serialVersionUID = 5575261745655809684L;
	
	@Getter
	private final HttpStatus status = HttpStatus.UNAUTHORIZED;

	public ZUnauthorize(String message) {
		super(message);
	}
	
	
}
