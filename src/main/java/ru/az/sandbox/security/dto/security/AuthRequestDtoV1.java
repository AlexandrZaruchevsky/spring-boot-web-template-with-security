package ru.az.sandbox.security.dto.security;

public record AuthRequestDtoV1(
		String username,
		String password
) {}
