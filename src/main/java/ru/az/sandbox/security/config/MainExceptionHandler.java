package ru.az.sandbox.security.config;


import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.az.sandbox.security.dto.security.ExceptionResponseV1;
import ru.az.sandbox.security.model.exceptions.ZException;

@RestControllerAdvice
public class MainExceptionHandler{

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ExceptionResponseV1> error(UsernameNotFoundException ex){
		ExceptionResponseV1 exResp = new ExceptionResponseV1(
				HttpStatus.UNAUTHORIZED.value(), 
				HttpStatus.UNAUTHORIZED.name(), 
				ex.getMessage());
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(exResp);
	}
	
	@ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
	public ResponseEntity<ExceptionResponseV1> handleAuthenticationCredentialsNotFoundException(
			AuthenticationCredentialsNotFoundException ex){
		ExceptionResponseV1 exResp = new ExceptionResponseV1(
				HttpStatus.UNAUTHORIZED.value(), 
				HttpStatus.UNAUTHORIZED.name(), 
				ex.getMessage());
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(exResp);
	}
	
	@ExceptionHandler(PSQLException.class)
	public ResponseEntity<ExceptionResponseV1> handlePSQLException(PSQLException ex){
		ExceptionResponseV1 exResp = new ExceptionResponseV1(
				HttpStatus.BAD_REQUEST.value(), 
				HttpStatus.BAD_REQUEST.name(), 
				ex.getMessage());
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(exResp);
	}
	
	@ExceptionHandler(ZException.class)
	public ResponseEntity<ExceptionResponseV1> handleZException(ZException ex){
		ExceptionResponseV1 exResp = new ExceptionResponseV1(
				ex.getStatus().value(), 
				ex.getStatus().name(), 
				ex.getMessage());
		return ResponseEntity
				.status(ex.getStatus())
				.body(exResp);
	}
	
	
}
