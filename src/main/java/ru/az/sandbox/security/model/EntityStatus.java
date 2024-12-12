package ru.az.sandbox.security.model;

import java.util.Arrays;
import java.util.Optional;

public enum EntityStatus {

	ACTIVE,
	NOT_ACTIVE,
	DELETED,
	UNDEFINED
	;
	
	public static EntityStatus getStatus(String str) {
		return Optional.ofNullable(str)
				.map(s -> Arrays.stream(values())
								.filter(es -> s.equalsIgnoreCase(es.name()))
								.findFirst()
								.orElse(UNDEFINED))
				.orElse(UNDEFINED);
	}
	
}
