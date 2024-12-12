package ru.az.sandbox.security.model;

import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;

public enum Permission {
	
	ADMIN_READ("admin:read"),
	ADMIN_WRITE("admin:write"),
	USER_READ("user:read"),
	USER_WRITE("user:write"),
	GUEST("guest")
	;
	
	@Getter
	private final String permission;
	
	private Permission(String permission) {
		this.permission = permission;
	}
	
	public static Permission getPermission(String str) {
		return Optional.ofNullable(str)
				.map(String::toUpperCase)
				.map(s -> Arrays.stream(values())
						.filter(p -> p.name().equals(s))
						.findFirst()
						.orElse(GUEST)
				).orElse(GUEST);
	}

}
