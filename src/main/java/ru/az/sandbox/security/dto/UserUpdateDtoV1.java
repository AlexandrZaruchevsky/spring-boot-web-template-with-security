package ru.az.sandbox.security.dto;

public record UserUpdateDtoV1(
		Long id,
		String email,
		String lastName,
		String firstName,
		String middleName
) {

}
