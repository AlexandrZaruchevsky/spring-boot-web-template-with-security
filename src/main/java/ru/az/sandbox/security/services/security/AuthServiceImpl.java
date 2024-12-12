package ru.az.sandbox.security.services.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.az.sandbox.security.dto.security.AuthReponseDtoV1;
import ru.az.sandbox.security.dto.security.AuthRequestDtoV1;
import ru.az.sandbox.security.security.JwtProvider;
import ru.az.sandbox.security.security.JwtUser;
import ru.az.sandbox.security.security.JwtUserService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final JwtUserService jwtUserService;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;

	private volatile Map<String, String> refreshStorage = Collections.synchronizedMap(new HashMap<>());
	
	@Override
	public AuthReponseDtoV1 login(AuthRequestDtoV1 authRequest) {
		JwtUser jwtUser = (JwtUser) jwtUserService.loadUserByUsername(authRequest.username());
		if (passwordEncoder.matches(authRequest.password(), jwtUser.getPassword())) {
			String accessToken = jwtProvider.generateAccessToken(jwtUser);
			String refreshToken = jwtProvider.generateRefreshToken(jwtUser);
			refreshStorage.put(authRequest.username(), refreshToken);
			AuthReponseDtoV1 authReponseDtoV1 = new AuthReponseDtoV1(accessToken, refreshToken);
			return authReponseDtoV1;
		} else {
			throw new AuthenticationCredentialsNotFoundException("Password invalid");
		}
	}

	@Override
	public AuthReponseDtoV1 getAccesToken(String refreshToken) {
		if(jwtProvider.validateRefreshToken(refreshToken)) {
			final String username = jwtProvider.getUsernameFromRefreshToken(refreshToken);
			final String tokenFromStorage = refreshStorage.get(username);
			if(tokenFromStorage != null && tokenFromStorage.equals(refreshToken)) {
				final JwtUser jwtUser = (JwtUser) jwtUserService.loadUserByUsername(username);
				final String accessToken = jwtProvider.generateAccessToken(jwtUser);
				return new AuthReponseDtoV1(accessToken, null);
			}
		}
		return new AuthReponseDtoV1(null, null);
	}

	@Override
	public AuthReponseDtoV1 refresh(String refreshToken) {
		if(jwtProvider.validateRefreshToken(refreshToken)) {
			final String username = jwtProvider.getUsernameFromRefreshToken(refreshToken);
			final String tokenFromStorage = refreshStorage.get(username);
			if(tokenFromStorage != null && tokenFromStorage.equals(refreshToken)) {
				final JwtUser jwtUser = (JwtUser) jwtUserService.loadUserByUsername(username);
				final String accessTokenNew = jwtProvider.generateAccessToken(jwtUser);
				final String refreshTokenNew = jwtProvider.generateRefreshToken(jwtUser);
				refreshStorage.put(jwtUser.getUsername(), refreshTokenNew);
				return new AuthReponseDtoV1(accessTokenNew, refreshTokenNew);
			}
		}
		throw new AuthenticationCredentialsNotFoundException("Invalid token");
	}

	@Override
	public Authentication getAuthInfo() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
