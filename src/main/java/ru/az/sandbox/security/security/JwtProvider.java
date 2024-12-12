package ru.az.sandbox.security.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtProvider {

	private final SecretKey jwtAccessSecret;
	private final SecretKey jwtRefreshSecret;
	private final int jwtAccessTokenExpiration;
	private final int jwtRefreshTokenExpiration;

	public JwtProvider(
			@Value("${app.security.jwt-access-secret:123456789}") String jwtAccessSecret,
			@Value("${app.security.jwt-access-secret:987654321}") String jwtRefreshSecret,
			@Value("${app.security.jwt-access-token-expiration:5}") int jwtAccessTokenExpiration,
			@Value("${app.security.jwt-refresh-token-expiration:5}") int jwtRefreshTokenExpiration
	) {
		this.jwtAccessSecret = Keys.hmacShaKeyFor(jwtAccessSecret.getBytes());
		this.jwtRefreshSecret = Keys.hmacShaKeyFor(jwtRefreshSecret.getBytes());
		this.jwtAccessTokenExpiration = jwtAccessTokenExpiration;
		this.jwtRefreshTokenExpiration = jwtRefreshTokenExpiration;
	}

	public String generateAccessToken(@NonNull JwtUser jwtUser) {
		List<String> authorities = jwtUser.getAuthorities().stream()
			.map(authority -> authority.getAuthority())
			.toList();
		final var now = LocalDateTime.now();
		final var accessExpirationInstant = now.plusSeconds(jwtAccessTokenExpiration)
				.atZone(ZoneId.systemDefault())
				.toInstant();
		final var accessExpiration = Date.from(accessExpirationInstant);
		return Jwts.builder()
				.subject(jwtUser.getUsername())
				.issuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
				.expiration(accessExpiration)
				.signWith(jwtAccessSecret)
				.claim("owner", jwtUser.getUser().getFio())
				.claim("roles", authorities)
				.compact();
	}

	public String generateRefreshToken(@NonNull JwtUser jwtUser) {
		final var now = LocalDateTime.now();
		final var refreshExpirationInstant = now.plusSeconds(jwtRefreshTokenExpiration).atZone(ZoneId.systemDefault())
				.toInstant();
		final var refreshExpiration = Date.from(refreshExpirationInstant);
		return Jwts.builder()
				.subject(jwtUser.getUsername())
				.issuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
				.expiration(refreshExpiration)
				.signWith(jwtRefreshSecret).compact();
	}

	public String getUsernameFromAccessToken(String accessToken) {
		return extractClaim(accessToken, Claims::getSubject, jwtAccessSecret);
	}
	
	public String getUsernameFromRefreshToken(String refreshToken) {
		return extractClaim(refreshToken, Claims::getSubject, jwtRefreshSecret);
	}
	
	public Boolean validateAccessToken(String accessToken) {
		return validate(accessToken, jwtAccessSecret);
	}
	
	public boolean validate(String token, SecretKey secret) {
		try {
			Jwts.parser().verifyWith(secret).build()
				.parseSignedClaims(token);
				return true;
        } catch (ExpiredJwtException expEx) {
        	log.error("JWT was expired or incorrect");
        	throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
        } catch (UnsupportedJwtException unsEx) {
        	log.error("Unsupported JWT token");
        	throw new AuthenticationCredentialsNotFoundException("Unsupported JWT token");
        } catch (SecurityException | MalformedJwtException mjEx) {
        	log.error("Token expired");
        	throw new AuthenticationCredentialsNotFoundException("Token expired");
        } catch (IllegalArgumentException e) {
        	log.error("JWT token compact of handler are invalid");
        	throw new AuthenticationCredentialsNotFoundException("JWT token compact of handler are invalid");
        } catch (SignatureException e) {
        	log.error("SignatureException");
        	throw new AuthenticationCredentialsNotFoundException("JWT token compact of handler are invalid");
        } catch (Exception e) {
        	log.error("{}", e.getClass());
        	throw new AuthenticationCredentialsNotFoundException(e.getMessage());
	    }		
	}
	
	
	public Boolean validateRefreshToken(String refreshToken) {
		return validate(refreshToken, jwtRefreshSecret);
	}
	
	public List<SimpleGrantedAuthority> getAuthotorites(String accessToken) {
		Claims claims = getAccessClaims(accessToken);
		@SuppressWarnings("unchecked")
		List<String> list = claims.get("roles", List.class);
		return list.stream()
				.map(SimpleGrantedAuthority::new)
				.toList();
	}
	
	public Claims getAccessClaims(@NonNull String token) {
        return extractAllClaims(token, jwtAccessSecret);
    }
	
	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, SecretKey key) {
		final Claims claims = extractAllClaims(token, key);
		return claimsResolver.apply(claims);
	}

    private Claims extractAllClaims(String token, SecretKey key) {
    	return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
