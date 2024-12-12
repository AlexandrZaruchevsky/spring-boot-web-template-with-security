package ru.az.sandbox.security.dto;

public record UserRegistrationDtoV1(
	String username,
	String email,
	String password
) {

}
