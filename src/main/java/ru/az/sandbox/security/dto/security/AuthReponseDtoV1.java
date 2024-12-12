package ru.az.sandbox.security.dto.security;

public record AuthReponseDtoV1(
		String accessToken,
		String refreshToken
) {}
