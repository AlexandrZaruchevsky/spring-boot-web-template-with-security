package ru.az.sandbox.security.security;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.az.sandbox.security.model.EntityStatus;
import ru.az.sandbox.security.repo.UserRepoV1;

@Component
@RequiredArgsConstructor
public class JwtUserService implements UserDetailsService{

	private final UserRepoV1 userRepo;
	
	@Override
	@Cacheable(cacheNames = "security-users", key = "#username")
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo.findByUsernameAndStatus(username, EntityStatus.ACTIVE)
				.map(JwtUser::new)
				.orElseThrow(() -> new UsernameNotFoundException(
								String.format("User with username [ %s ] not found or user not active", username)));
	}

}
