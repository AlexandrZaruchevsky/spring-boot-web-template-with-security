package ru.az.sandbox.security.config;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;

import ru.az.sandbox.security.dto.ValidErrorResponseDtoV1;

@RestControllerAdvice
public class ValidateExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationExceptions(
	  MethodArgumentNotValidException ex) throws JsonProcessingException {
		Map<String, String> messages = ex.getFieldErrors().stream()
			.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
		ValidErrorResponseDtoV1 validError = new ValidErrorResponseDtoV1(
				"VALID_ERROR", 
				HttpStatus.BAD_REQUEST.value(), 
				HttpStatus.BAD_REQUEST.name(), 
				messages);
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(validError);
	}
	
}
