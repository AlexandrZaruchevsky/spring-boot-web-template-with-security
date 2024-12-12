package ru.az.sandbox.security.services;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.az.sandbox.security.model.Permission;
import ru.az.sandbox.security.model.Role;
import ru.az.sandbox.security.model.User;
import ru.az.sandbox.security.repo.RoleRepoV1;
import ru.az.sandbox.security.repo.UserRepoV1;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitServiceV1 {
	
	@Value("${app.security.super-user:admin}")
	private String userDefault;
	@Value("${app.security.super-user-email:admin}")
	private String emailDefault;
	@Value("${app.security.super-user-password:12345678}")
	private String passwordDefault;
	@Value("${app.security.super-user-role:SUPER_USER}")
	private String roleDefault;
	@Value("${app.security.guest-role}")
	private String roleGuest;

	private final UserRepoV1 userRepo;
	private final RoleRepoV1 roleRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Transactional
	public void initSecurity() {
		
		if (userRepo.count() > 0 || roleRepo.count() > 0) {
			log.warn("Security alredy initialized");
			return;
		}
		
		Role role = Role.builder()
			.roleName("SUPER_USER")
			.permissions(Set.copyOf(Arrays.asList(Permission.values())))
			.build();
		
		role = roleRepo.save(role);
		
		User user = User.builder()
			.username(userDefault)
			.email(emailDefault)
			.password(passwordEncoder.encode(passwordDefault))
			.roles(List.of(role))
			.build();
		
		userRepo.save(user);
		
		Role roleGuest = Role.builder().roleName(this.roleGuest).permissions(Set.of(Permission.GUEST)).build();
		roleRepo.save(roleGuest);
		
		log.info("Security initialize successfull");
		
	}
	
}
