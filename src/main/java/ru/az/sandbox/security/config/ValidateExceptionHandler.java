package ru.az.sandbox.security.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class ValidateExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationExceptions(
	  MethodArgumentNotValidException ex) throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		String string = mapper.writeValueAsString(ex.getFieldErrors().stream()
				.map(fe -> "field[" + fe.getField() + "] ::: " + fe.getDefaultMessage())
				.toList());
		
		return ResponseEntity
				.status(ex.getBody().getStatus())
				.body(string);
	}
	
}
