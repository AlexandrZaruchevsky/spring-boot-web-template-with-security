package ru.az.sandbox.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SecurityStarter {

	public static void main(String[] args) {
		SpringApplication.run(SecurityStarter.class, args);
	}
	
}
