package ru.az.sandbox.security.services.security;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;

import ru.az.sandbox.security.dto.security.AuthReponseDtoV1;
import ru.az.sandbox.security.dto.security.AuthRequestDtoV1;

public interface AuthService {
	
	AuthReponseDtoV1 login(@NonNull AuthRequestDtoV1 authRequest);
	AuthReponseDtoV1 getAccesToken(@NonNull String refreshToken);
	AuthReponseDtoV1 refresh(@NonNull String refreshToken);
	Authentication getAuthInfo();
	
}
