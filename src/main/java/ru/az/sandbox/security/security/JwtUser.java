package ru.az.sandbox.security.security;

import static ru.az.sandbox.security.model.EntityStatus.ACTIVE;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.az.sandbox.security.model.Permission;
import ru.az.sandbox.security.model.Role;
import ru.az.sandbox.security.model.User;

@RequiredArgsConstructor
public class JwtUser implements UserDetails{

	private static final long serialVersionUID = -6451572716319841991L;

	@Getter
	private final User user;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getRoles().stream()
				.map(Role::getPermissions)
				.flatMap(r -> r.stream())
				.distinct()
				.map(Permission::getPermission)
				
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return ACTIVE.equals(user.getStatus());
	}

	@Override
	public boolean isAccountNonLocked() {
		return ACTIVE.equals(user.getStatus());
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return ACTIVE.equals(user.getStatus());
	}

	@Override
	public boolean isEnabled() {
		return ACTIVE.equals(user.getStatus());
	}

}
