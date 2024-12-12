package ru.az.sandbox.security.api.v1.admin;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import ru.az.sandbox.security.dto.RoleDtoV1;
import ru.az.sandbox.security.model.exceptions.ZException;
import ru.az.sandbox.security.services.admin.AdminRoleServiceV1;

@RestController
@RequestMapping("api/v1/admins/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('admin:read')")
public class AdminRoleRestControllerV1 {
	
	private final AdminRoleServiceV1 adminRoleService;
	
	@GetMapping("list")
	public ResponseEntity<List<RoleDtoV1>> getRolesAsList(
			PageRequestDtoV2 request
	){
		return ResponseEntity.ok(adminRoleService.findAll(request));
	}
	
	@GetMapping
	public ResponseEntity<Page<RoleDtoV1>> getRolesAsPage(
			PageRequestDtoV2 request
	){
		return ResponseEntity.ok(adminRoleService.findAllAsPage(request));
	}

	@PostMapping
	@PreAuthorize("hasAuthority('admin:write')")
	public ResponseEntity<RoleDtoV1> create(
			@RequestBody RoleDtoV1 roleDto,
			HttpServletRequest request
	) throws ZException{
		RoleDtoV1 reponse = adminRoleService.create(roleDto);
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
	public ResponseEntity<RoleDtoV1> update(
			@RequestBody RoleDtoV1 roleDto
	) throws ZException{
		RoleDtoV1 response = adminRoleService.update(roleDto);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority('admin:write')")
	public ResponseEntity<RoleDtoV1> findById(
			@PathVariable Long id
	) throws ZException{
		return ResponseEntity.ok(adminRoleService.findById(id));
	}
	
	@DeleteMapping("{id}")
	@PreAuthorize("hasAuthority('admin:write')")
	public ResponseEntity<?> deleteById(
			@PathVariable Long id	
	) throws ZException{
		adminRoleService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("{id}/{status}")
	@PreAuthorize("hasAuthority('admin:write')")
	public ResponseEntity<?> changeStatus(
			@PathVariable Long id,
			@PathVariable String status
	) throws ZException{
		adminRoleService.changeEntityStatus(id, status);
		return ResponseEntity.noContent().build();
	}
	
}
