package ru.az.sandbox.security.dto;

import java.util.List;
import java.util.Optional;

import ru.az.sandbox.security.model.User;

public record UserResponseDtoV1(
		Long id,
		String username,
		String email,
		String lastName,
		String firstName,
		String middleName,
		List<RoleDtoV1> roles,
		String status
) {

	public static UserResponseDtoV1 create(User user) {
		return Optional.ofNullable(user)
				.map(u -> {
					return new UserResponseDtoV1(
							u.getId(),
							u.getUsername(), 
							u.getEmail(), 
							u.getLastName(), 
							u.getFirstName(), 
							u.getMiddleName(),
							List.of(),
							u.getStatus().name());
				}).orElse(null);
	}
	
	public static UserResponseDtoV1 createWithRoles(User user) {
		return Optional.ofNullable(user)
				.map(u -> {
					return new UserResponseDtoV1(
							u.getId(),
							u.getUsername(), 
							u.getEmail(), 
							u.getLastName(), 
							u.getFirstName(), 
							u.getMiddleName(),
							Optional.ofNullable(u.getRoles())
								.map(roles -> roles.stream().map(RoleDtoV1::create).toList())
								.orElse(List.of()),
							u.getStatus().name()
							);
				}).orElse(null);
	}
	
}
