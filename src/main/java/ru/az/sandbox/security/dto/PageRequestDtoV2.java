package ru.az.sandbox.security.dto;

import java.util.Optional;

import ru.az.sandbox.security.model.EntityStatus;

public record PageRequestDtoV2(
		Integer pageNumber,
		Integer pageSize,
		String filter,
		String sortBy,
		String statusEntity,
		Boolean isAllEntity
) {

	public PageRequestDtoV2{
		pageNumber = Optional.ofNullable(pageNumber).orElse(0);
		pageSize = Optional.ofNullable(pageSize).orElse(10);
		filter = Optional.ofNullable(filter).map(String::trim).orElse("");
		sortBy = Optional.ofNullable(sortBy).orElse("NONE");
		statusEntity = Optional.ofNullable(statusEntity)
				.map(s -> EntityStatus.getStatus(s).equals(EntityStatus.UNDEFINED)
						? EntityStatus.ACTIVE.name()
						: s
				).orElse(EntityStatus.ACTIVE.name());
		isAllEntity = Optional.ofNullable(isAllEntity).orElse(false);
	}
	
}
