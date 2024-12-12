package ru.az.sandbox.security.dto.security;

public record ChangePasswordRequestV1(
	String username,
	String oldPassword,
	String newPassword
) {}
