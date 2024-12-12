package ru.az.sandbox.security.cl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.az.sandbox.security.services.InitServiceV1;

@Component
@Order(1)
@RequiredArgsConstructor
public class SecurityInitCL implements CommandLineRunner{

	private final InitServiceV1 initService;

	@Override
	public void run(String... args) throws Exception {
		
		initService.initSecurity();
	}
	
}
