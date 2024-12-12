package ru.az.sandbox.security.api.v1.security;

import java.net.URI;
import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.az.sandbox.security.dto.UserRegistrationDtoV1;
import ru.az.sandbox.security.dto.UserResponseDtoV1;
import ru.az.sandbox.security.dto.security.AuthRequestDtoV1;
import ru.az.sandbox.security.dto.security.TokenRequestDtoV1;
import ru.az.sandbox.security.model.exceptions.ZException;
import ru.az.sandbox.security.model.exceptions.ZNotFoundEntityException;
import ru.az.sandbox.security.services.UserServiceV1;
import ru.az.sandbox.security.services.security.AuthService;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthRestControllerV1 {

	private final AuthService authService;
	private final UserServiceV1 userService;
	
	@PostMapping("login")
	public ResponseEntity<?> login(
			@RequestBody AuthRequestDtoV1 authRequest
	) throws ZNotFoundEntityException{
		return ResponseEntity.ofNullable(authService.login(authRequest));
	}
	
	@PostMapping("access-token")
	public ResponseEntity<?> accessToken(
			@RequestBody TokenRequestDtoV1 tokenRequest 
	) throws ZNotFoundEntityException{
		return ResponseEntity.ofNullable(authService.getAccesToken(tokenRequest.refreshToken()));
	}
	
	@PostMapping("refresh-token")
	public ResponseEntity<?> refreshToken(
			@RequestBody TokenRequestDtoV1 tokenRequest 
	) throws ZNotFoundEntityException{
		return ResponseEntity.ofNullable(authService.refresh(tokenRequest.refreshToken()));
	}
	
	@PostMapping("registration")
	public ResponseEntity<?> registration(
			@RequestBody UserRegistrationDtoV1 registration,
			HttpServletRequest request
	) throws ZException{
		UserResponseDtoV1 response = userService.registration(registration);
		URI uri = UriComponentsBuilder
			.fromUriString("/api/v1/users/")
			.path("{id}")
			.buildAndExpand(Collections.singletonMap("id", response.id()))
			.toUri();
		return ResponseEntity
				.created(uri)
				.body(response);
	}
	
}
