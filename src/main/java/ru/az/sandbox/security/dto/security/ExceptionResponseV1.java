package ru.az.sandbox.security.dto.security;

public record ExceptionResponseV1(
		Integer statusCode,
		String statusName,
		String message
) {}
