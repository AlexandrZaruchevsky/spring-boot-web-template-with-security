package ru.az.sandbox.security.api.v1.admin;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.az.sandbox.security.dto.PageRequestDtoV2;
import ru.az.sandbox.security.dto.UserCreateOrUpdateDtoV1;
import ru.az.sandbox.security.dto.UserResponseDtoV1;
import ru.az.sandbox.security.model.exceptions.ZException;
import ru.az.sandbox.security.services.admin.AdminUserServiceV1;

@RestController
@RequestMapping("api/v1/admins/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('admin:read')")
public class AdminUserRestControllerV1 {
	
	private final AdminUserServiceV1 adminUserService;

	@GetMapping("list")
	public ResponseEntity<List<UserResponseDtoV1>> getRolesAsList(
			PageRequestDtoV2 request
	){
		return ResponseEntity.ok(adminUserService.findAll(request));
	}
	
	@GetMapping
	public ResponseEntity<Page<UserResponseDtoV1>> getRolesAsPage(
			PageRequestDtoV2 request
	){
		return ResponseEntity.ok(adminUserService.findAllAsPage(request));
	}

	@PostMapping
	@PreAuthorize("hasAuthority('admin:write')")
	public ResponseEntity<UserResponseDtoV1> create(
			@Validated(UserCreateOrUpdateDtoV1.Create.class) @RequestBody UserCreateOrUpdateDtoV1 body,
			HttpServletRequest request
	) throws ZException{
		var reponse = adminUserService.create(body);
		URI uri = UriComponentsBuilder.fromUri(URI.create(request.getRequestURI()))
			.path("{id}").buildAndExpand(Collections.singletonMap("id", reponse.id())).toUri();
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.location(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.body(reponse);
	}
	
	@PutMapping
	@PreAuthorize("hasAuthority('admin:write')")
	public ResponseEntity<UserResponseDtoV1> update(
			@Validated(UserCreateOrUpdateDtoV1.Update.class) @RequestBody UserCreateOrUpdateDtoV1 body
	) throws ZException{
		var response = adminUserService.update(body);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority('admin:write')")
	public ResponseEntity<UserResponseDtoV1> findById(
			@PathVariable Long id
	) throws ZException{
		return ResponseEntity.ok(adminUserService.findById(id));
	}
	
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority('admin:write')")
	public ResponseEntity<?> deleteById(
			@PathVariable Long id	
	) throws ZException{
		adminUserService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("{id}/{status}")
	@PreAuthorize("hasAuthority('admin:write')")
	public ResponseEntity<?> changeStatus(
			@PathVariable Long id,
			@PathVariable String status
	) throws ZException{
		adminUserService.changeEntityStatus(id, status);
		return ResponseEntity.noContent().build();
	}
	
}
