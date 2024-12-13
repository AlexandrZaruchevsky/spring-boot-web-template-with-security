package ru.az.sandbox.security.dto;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.az.sandbox.security.model.Permission;
import ru.az.sandbox.security.model.Role;

public record RoleDtoV1(
		Long id,
		@NotNull
		@Size(min = 4, max = 64)
		String roleName,
		List<String> permissions,
		String status
) {
	public RoleDtoV1{
		id = Optional.ofNullable(id).orElse(-1l);
		roleName = Optional
					.ofNullable(roleName)
					.map(s -> s.replaceAll("\\s+", "").toUpperCase())
					.orElse("ROLE_NONAME");
		permissions = Optional.ofNullable(permissions).orElse(List.of());
	}
	
	public static RoleDtoV1 create(Role role) {
		return Optional.ofNullable(role)
				.map(r -> {
					return new RoleDtoV1(
							r.getId(), 
							r.getRoleName(), 
							Optional.ofNullable(r.getPermissions())
								.map(p -> p.stream().map(Permission::name).toList())
								.orElse(List.of()),
							r.getStatus().name()
							);
				}).orElse(null);
	}
	
}
