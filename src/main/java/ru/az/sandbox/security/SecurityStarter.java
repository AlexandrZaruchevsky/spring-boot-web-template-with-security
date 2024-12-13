package ru.az.sandbox.security;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableCaching
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class SecurityStarter {

	public static void main(String[] args) {
		SpringApplication.run(SecurityStarter.class, args);
	}
	
}
