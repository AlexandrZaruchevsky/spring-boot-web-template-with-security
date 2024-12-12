package ru.az.sandbox.security.dto;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;

public record UserCreateOrUpdateDtoV1(
		Long id,
		@NotNull(
				message = "username may not be null",
				groups = { Create.class, Update.class }
		) String username,
		@NotNull(
				message = "email may not be null",
				groups = { Create.class, Update.class }
		) String email,
		@NotNull(
				message = "password may not be null",
				groups = { Create.class }
		) String password,
		String lastName,
		String firstName,
		String middleName,
		List<Long> roles
) {
	
	public UserCreateOrUpdateDtoV1{
		id = Optional.ofNullable(id).orElse(-1l);
		roles = Optional.ofNullable(roles).orElse(List.of());
	}

	public interface Create{}
	
	public interface Update{}
	
}
