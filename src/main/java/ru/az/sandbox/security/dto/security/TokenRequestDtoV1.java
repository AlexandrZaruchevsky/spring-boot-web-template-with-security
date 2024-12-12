package ru.az.sandbox.security.dto.security;

public record TokenRequestDtoV1(
		String accessToken,
		String refreshToken
		) {

}
