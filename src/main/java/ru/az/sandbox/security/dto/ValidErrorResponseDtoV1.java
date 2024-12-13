package ru.az.sandbox.security.dto;

import java.util.Map;

public record ValidErrorResponseDtoV1(
		String typeError,
		Integer statusCode,
		String statusName,
		Map<String, String> messages
) {}
