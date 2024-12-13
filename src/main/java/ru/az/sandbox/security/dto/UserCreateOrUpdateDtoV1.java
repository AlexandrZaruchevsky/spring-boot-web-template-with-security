package ru.az.sandbox.security.dto;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateOrUpdateDtoV1(
		Long id,
		@NotNull(groups = { Create.class, Update.class }) 
		@Size(min = 4, max = 64, groups = { Create.class, Update.class })
		String username,
		@NotNull(groups = { Create.class, Update.class })
		@Size(min = 4, max = 256, groups = { Create.class, Update.class })
		String email,
		@NotNull(groups = { Create.class }) 
		@Size(min = 8, max = 128, groups = { Create.class})
		String password,
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
