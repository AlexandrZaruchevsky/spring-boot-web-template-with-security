package ru.az.sandbox.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import lombok.RequiredArgsConstructor;
import ru.az.sandbox.security.security.JwtFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	private final JwtFilter jwtFilter;
	private final String[] urlPermitAll = {
			"/api/v1/auth/login",
			"/api/v1/auth/access-token",
			"/api/v1/auth/refresh-token",
			"/api/v1/auth/registration"
	};
	
	@Autowired
	@Qualifier("handlerExceptionResolver")
	private HandlerExceptionResolver resolver;


	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		return http
				.csrf(csrf -> csrf.disable())
				.httpBasic(hb -> hb.disable())
				.formLogin(Customizer.withDefaults())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(
						auth -> auth
							.requestMatchers(urlPermitAll).permitAll()
							.anyRequest().authenticated()
				)
				.exceptionHandling(exH -> exH
							.authenticationEntryPoint((request, response, authException) -> {
								resolver.resolveException(request, response, null, authException);
							})
				)
				.addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
    }

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(BCryptVersion.$2B);
	}
	
}
