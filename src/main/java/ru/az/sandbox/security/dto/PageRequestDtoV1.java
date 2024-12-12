package ru.az.sandbox.security.dto;

import java.util.Optional;

public record PageRequestDtoV1(
		Integer pageNumber,
		Integer pageSize,
		String filter,
		String sortBy
) {

	public PageRequestDtoV1{
		pageNumber = Optional.ofNullable(pageNumber).orElse(0);
		pageSize = Optional.ofNullable(pageSize).orElse(10);
		filter = Optional.ofNullable(filter).map(String::trim).orElse("");
		sortBy = Optional.ofNullable(sortBy).orElse("NONE");
	}
	
}
